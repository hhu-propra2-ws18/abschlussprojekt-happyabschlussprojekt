package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.MailService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = {"/user"})
public class UserController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private GeraetService geraetService;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    PersonService personService;
    @Autowired
    NotificationService notificationService;

    public UserController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Ihr Benutzername oder Kennwort sind nicht gültig.");

        if (logout != null)
            model.addAttribute("message", "Sie wurden erfolgreich abgemeldet.");
        model.addAttribute("person", new Person());
        return "default/login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String name = principal.getName();
        notificationService.updateAnzahl(name);
        Person person = personRepository.findByUsername(name).get();
        if (person.getFoto().getBild().length > 0) {
            person.setEncode(person.getFoto().encodeBild());
        }

        model.addAttribute("person", person);
        if (name.equals("admin")) {
            return "redirect://localhost:8080/admin";
        } else {
            return "user/profile";
        }
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal) {
        String name = principal.getName();
        notificationService.updateAnzahl(name);
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        model.addAttribute("geraete", geraetService.getAllByBesitzerWithBilder(name));
        return "user/myThings";
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal) {
        String mieterName = principal.getName();
        notificationService.updateAnzahl(mieterName);

        Person person = personRepository.findByUsername(mieterName).get();
        model.addAttribute("person", person);

        List<RentEvent> activeRentEvents = rentEventRepository.findAllByMieterAndReturnStatus(mieterName, ReturnStatus.ACTIVE);
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(mieterName, ReturnStatus.DEADLINE_CLOSE));
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(mieterName, ReturnStatus.DEADLINE_OVER));
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(mieterName, ReturnStatus.KAPUTT));

        List<GeraetWithRentEvent> activeGeraete = new ArrayList<>();
        for (RentEvent rentEvent : activeRentEvents) {
            GeraetWithRentEvent geraetWithRentEvent = new GeraetWithRentEvent();
            geraetWithRentEvent.setGeraet(geraetRepository.findById(rentEvent.getGeraetId()).get());
            geraetWithRentEvent.setRentEvent(rentEvent);
            activeGeraete.add(geraetWithRentEvent);
            if (geraetWithRentEvent.getGeraet().getBilder().get(0).getBild().length > 0) {
                geraetWithRentEvent.getGeraet().setEncode(geraetWithRentEvent.getGeraet().getBilder().get(0).encodeBild());
            }
        }

        List<RentEvent> bookedRentEvents = rentEventRepository.findAllByMieterAndReturnStatus(mieterName, ReturnStatus.BOOKED);
        List<GeraetWithRentEvent> bookedGeraete = new ArrayList<>();
        for (RentEvent rentEvent : bookedRentEvents) {
            GeraetWithRentEvent geraetWithRentEvent = new GeraetWithRentEvent();
            geraetWithRentEvent.setGeraet(geraetRepository.findById(rentEvent.getGeraetId()).get());
            geraetWithRentEvent.setRentEvent(rentEvent);
            bookedGeraete.add(geraetWithRentEvent);
            if (geraetWithRentEvent.getGeraet().getBilder().get(0).getBild().length > 0) {
                geraetWithRentEvent.getGeraet().setEncode(geraetWithRentEvent.getGeraet().getBilder().get(0).encodeBild());
            }
        }


        model.addAttribute("activeGeraete", activeGeraete);
        model.addAttribute("bookedGeraete", bookedGeraete);
        return "user/rentThings";
    }

    @GetMapping("/notifications")
    public String makeNotifications(Model model, Principal principal) {
        String name = principal.getName();
        notificationService.updateAnzahl(name);

        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        model.addAttribute("notifications", notificationService.findAllByBesitzer(name));
        return "user/notifications";
    }

    @GetMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id, Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        Geraet geraet1 = geraetRepository.findById(id).get();

        Account account = accountRepository.findByAccount(name).get();
        model.addAttribute("account", account);

        model.addAttribute("geraet", geraet1);
        model.addAttribute("notification", new Notification());
        return "user/anfragen";
    }

    @PostMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id, @ModelAttribute Notification notification,
                           Principal principal) throws Exception {

        Notification newNotification = new Notification();
        newNotification.setType("request");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setMessage(notification.getMessage());
        newNotification.setMietezeitpunktStart(new Date(notification.getMietezeitpunktStart().getTime() + 60 * 60 * 6000));
        newNotification.setMietezeitpunktEnd(new Date(notification.getMietezeitpunktEnd().getTime() + 60 * 60 * 6000));
        newNotification.setBesitzer(notification.getBesitzer());

        Geraet geraet = geraetRepository.findById(newNotification.getGeraetId()).get();
        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();

        newNotification.setEncode(geraet.getEncode());
        notificationRepository.save(newNotification);

        mailService.sendAnfragMail(person, geraet, principal);

        return "redirect://localhost:8080";
    }

    @GetMapping("/addGeraet")
    public String addGeraet(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();

        model.addAttribute("person", person);
        return "user/addGeraet";
    }

    @PostMapping("/addGeraet")
    public String confirmGeraet(@ModelAttribute("geraet") Geraet geraet,
                                @RequestParam("files") MultipartFile[] files, Principal principal) throws IOException {
        List<Bild> bilds = new ArrayList<>();
        for (MultipartFile file : files) {
            Bild bild = new Bild();
            bild.setBild(file.getBytes());
            bilds.add(bild);
        }
        RentEvent verfuegbar = new RentEvent();
        verfuegbar.setTimeInterval(geraetService.convertToCET(new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd())));
        geraet.setBilder(bilds);
        geraet.setLikes(0);
        geraet.setBesitzer(principal.getName());
        System.out.println("***********************************\n" + geraet);
        Person person = personRepository.findByUsername(principal.getName()).get();
        int aktionPunkte = person.getAktionPunkte();
        person.setAktionPunkte(aktionPunkte + 10);

        geraetRepository.save(geraet);

        geraet.getVerfuegbareEvents().add(verfuegbar);
        geraetRepository.save(geraet);

        return "redirect://localhost:8080/user/myThings";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        proPayService.saveAccount(person.getUsername());
        model.addAttribute("person", person);
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        List<Transaction> transactions = proPayService.getAllTransactionForPerson(name);
        model.addAttribute("transactions", transactions);
        model.addAttribute("account", account);
        return "user/proPay";
    }

    @PostMapping("/propayErhoehung")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account) throws IOException {
        proPayService.erhoeheAmount(account, amount);
        return "redirect://localhost:8080";
    }

    @GetMapping("/geraet/{id}")
    public String geraet(@PathVariable Long id, Model model, Principal principal) {
        String person = principal.getName();
        Geraet geraet = geraetRepository.findById(id).get();
        Person personForComment = personRepository.findByUsername(geraet.getBesitzer()).get();
        List<String> encodes = geraetService.geraetBilder(geraet);
        model.addAttribute("encodes", encodes);
        model.addAttribute("person", personRepository.findByUsername(person).get());
        model.addAttribute("geraet", geraet);
        model.addAttribute("personForComment", personForComment);
        return "user/geraet";
    }

    @GetMapping("/BesitzerInfo/{id}")
    public String besitzerInfo(@PathVariable Long id, Model model) {
        Person besitzer = personRepository.findById(id).get();
        if (besitzer.getFoto().getBild().length > 0) {
            besitzer.setEncode(besitzer.getFoto().encodeBild());
        }

        model.addAttribute("comments", besitzer.getComments());
        model.addAttribute("person", besitzer);
        return "user/besitzerInfo";
    }

    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Person person = personRepository.findByUsername(geraetRepository.findById(id).get().getBesitzer()).get();

        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "user/edit";
    }

    @GetMapping("/geraet/zurueckgeben/{id}")
    public String geraetZurueck(@PathVariable Long id, Principal principal) throws Exception {
        RentEvent rentEvent = rentEventRepository.findById(id).get();
        rentEvent.setReturnStatus(ReturnStatus.WAITING_FOR_CONFIRMATION);
        rentEventRepository.save(rentEvent);

        Geraet geraet = geraetRepository.findById(rentEvent.getGeraetId()).get();

        Notification newNotification = new Notification();
        newNotification.setType("return");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setRentEventId(id);
        newNotification.setBesitzer(geraet.getBesitzer());
        notificationRepository.save(newNotification);

        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();
        mailService.sendReturnMail(person, geraet);

        return "redirect://localhost:8080/user/rentThings";
    }

    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect://localhost:8080/user/myThings";
    }

    @PostMapping("/notification/refuseRequest/{id}")
    public String notificationRefuseRequest(@PathVariable Long id) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        String mieter = notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();

        Person person = personRepository.findByUsername(mieter).get();

        mailService.sendRefuseRequestMail(person, geraet);
        notificationRepository.deleteById(id);
        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id) throws Exception {

        Notification notification = notificationRepository.findById(id).get();
        String mieter = notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        int reservationId = proPayService.erzeugeReservation(mieter, geraet.getBesitzer(), (int) geraet.getKaution());

        TimeInterval timeInterval = new TimeInterval(notification.getMietezeitpunktStart(), notification.getMietezeitpunktEnd());
        RentEvent rentEvent = new RentEvent();
        rentEvent.setMieter(mieter);
        rentEvent.setTimeInterval(timeInterval);
        rentEvent.setGeraetId(geraet.getId());
        rentEvent.setReservationId(reservationId);
        rentEvent.setReturnStatus(ReturnStatus.BOOKED);
        geraet.getRentEvents().add(rentEvent);
        int index = personService.positionOfFreeBlock(geraet, rentEvent);
        personService.intervalZerlegen(geraet, index, rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.save(rentEvent);
        notificationRepository.deleteById(id);

        Person person = personRepository.findByUsername(mieter).get();
        mailService.sendAcceptRequestMail(person, geraet);

        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        RentEvent rentEvent = rentEventRepository.findById(notification.getRentEventId()).get();
        rentEvent.setReturnStatus(ReturnStatus.KAPUTT);
        rentEventRepository.save(rentEvent);

        Person person = personRepository.findByUsername(rentEvent.getMieter()).get();
        Geraet geraet = geraetRepository.findById(rentEvent.getGeraetId()).get();
        mailService.sendRefuseReturnMail(person, geraet);
        personService.makeComment(geraet, person, grund);
        notificationRepository.deleteById(id);

        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification = notificationRepository.findById(id).get();

        RentEvent rentEvent = rentEventRepository.findById(notification.getRentEventId()).get();
        Geraet geraet = geraetRepository.findById(rentEvent.getGeraetId()).get();

        Person person = personRepository.findByUsername(rentEvent.getMieter()).get();
        mailService.sendAcceptReturnMail(person, geraet);
        personService.makeComment(geraet, person, grund);
//        geraetService.checkForTouchingIntervals(geraet, rentEvent);
        double amount = rentEvent.getTimeInterval().getDuration() * geraet.getKosten();
        proPayService.ueberweisen(notification.getAnfragePerson(), notification.getBesitzer(), (int) amount);
        proPayService.releaseReservation(rentEvent.getMieter(), rentEvent.getReservationId());
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.deleteById(notification.getRentEventId());

        notificationRepository.deleteById(id);
        return "redirect://localhost:8080/user/notifications";
    }

    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        return "user/changeProfile";
    }

    @PostMapping("/PersonInfo/Profile/ChangeProfile")
    public String chageProfile(Model model, @RequestParam("file") MultipartFile file,
                               @ModelAttribute("person") Person p, Principal principal) throws IOException {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setNachname(p.getNachname());
        person.setKontakt(p.getKontakt());
        person.setVorname(p.getVorname());
        person.setAdresse(p.getAdresse());
        personRepository.save(person);
        model.addAttribute("person", person);
        return "default/confirmationOfRegistration";
    }

    @GetMapping("/geraet/addLikes/{id}")
    public String like(@PathVariable Long id) {
        Geraet geraet = geraetRepository.findById(id).get();
        geraet.setLikes(geraet.getLikes() + 1);
        geraetRepository.save(geraet);
        return "redirect://localhost:8080";
    }

    @PostMapping("/geraet/edit/{id}")
    public String geraetEdit(Model model, @PathVariable Long id, @ModelAttribute Geraet geraet,
                             @RequestParam("files") MultipartFile[] files) throws IOException {
        Geraet geraet1 = geraetRepository.findById(id).get();
        List<Bild> bilds = new ArrayList<>();
        for (MultipartFile file : files) {
            Bild bild = new Bild();
            bild.setBild(file.getBytes());
            bilds.add(bild);
        }
        geraet1.setBilder(bilds);
        geraet1.setKosten(geraet.getKosten());
        geraet1.setTitel(geraet.getTitel());
        geraet1.setBeschreibung(geraet.getBeschreibung());
        geraet1.setKaution(geraet.getKaution());
        geraet1.setAbholort(geraet.getAbholort());
        System.out.println("***********************************\n" + geraet1);
        geraetRepository.save(geraet1);
        List<Geraet> geraete = null;
        model.addAttribute("geraete", geraete);
        return "redirect://localhost:8080/user/myThings";
    }
}


// DAS IST OPTIONAL
/*    @PostMapping("/propay")
    public String propay(Principal principal, @ModelAttribute("transferRequest") TransferRequest transferRequest){
        transferRequest.setUsername(principal.getName());
        transferRequestRepository.save(transferRequest);
        return "redirect:/";
    }*/

//@GetMapping("/bezahlen/{id}")
//public String bezahlen(@ModelAttribute Geraet geraet, Principal person, @PathVariable Long id) {
//    Geraet geraet1 = geraetRepository.findById(id).get();
//    String mieterName = person.getName();
//    geraet1.setMieter(mieterName);
//    geraetRepository.save(geraet1);
//    return "user/confirmBezahlen";
//}

