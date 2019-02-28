package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
@ControllerAdvice
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
    private PersonService personService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RentEventService rentEventService;

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        notificationService.updateAnzahlOfNotifications(person);
        person.checkPhoto();
        model.addAttribute("person", person);

        if (person.getRole().equals("ROLE_ADMIN")) {
            return "redirect://localhost:8080/admin";
        }
        return "user/profile";
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        notificationService.updateAnzahlOfNotifications(person);
        model.addAttribute("person", person);
        model.addAttribute("geraete", geraetService.getAllByBesitzerWithBilder(person));
        return "user/myThings";
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal) {
        Person mieter = personService.findByPrincipal(principal);
        notificationService.updateAnzahlOfNotifications(mieter);

        List<RentEvent> activeRentEvents = rentEventService.getActiveEventsForPerson(mieter);
        List<GeraetWithRentEvent> activeGeraete = new ArrayList<>();
        personService.checksActiveOrInActiveRentEvent(activeRentEvents, activeGeraete);

        List<RentEvent> bookedRentEvents = rentEventRepository.findAllByMieterAndReturnStatus(mieter, ReturnStatus.BOOKED);
        List<GeraetWithRentEvent> bookedGeraete = new ArrayList<>();

        personService.checksActiveOrInActiveRentEvent(bookedRentEvents, bookedGeraete);
        model.addAttribute("person", mieter);
        model.addAttribute("activeGeraete", activeGeraete);
        model.addAttribute("bookedGeraete", bookedGeraete);
        return "user/rentThings";
    }

    @GetMapping("/notifications")
    public String makeNotifications(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        notificationService.updateAnzahlOfNotifications(person);
        model.addAttribute("person", person);
        List<Notification> notificationList = notificationService.findAllByBesitzer(person);
        model.addAttribute("notifications", notificationList);
        return "user/notifications";
    }

    @GetMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id, Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        Geraet geraet = geraetRepository.findById(id).get();
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "user/anfragen";
    }

    @GetMapping("/geraet/changeToRent/{id}")
    public String changeToRent(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("person", geraet.getBesitzer());
        model.addAttribute("geraet", geraet);
        return "user/changeToRent";
    }

    @PostMapping("/geraet/changeToRent/{id}")
    public String changeToRent(@PathVariable Long id, @ModelAttribute("geraet") Geraet geraet, @RequestParam(value = "files",required = false) MultipartFile[] files) throws IOException {
        RentEvent verfuegbar = new RentEvent();
        TimeInterval timeIntervalWithout = new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd());
        TimeInterval timeInterval = geraetService.convertToCET(timeIntervalWithout);
        verfuegbar.setTimeInterval(timeInterval);

        Geraet geraet1 = geraetRepository.findById(id).get();
        geraetService.saveGeraet(files, geraet, id, false);
        //List<Bild> bilds = new ArrayList<>();
        //personService.umwechsleMutifileZumBild(files, bilds);
        //geraet1.setBilder(bilds);
        //geraet1.setKosten(geraet.getKosten());
        //geraet1.setTitel(geraet.getTitel());
        //geraet1.setBeschreibung(geraet.getBeschreibung());
        //geraet1.setKaution(geraet.getKaution());
        //geraet1.setForsale(false);
        //geraet1.setAbholort(geraet.getAbholort());
        //geraetRepository.save(geraet1);
        geraet1.getVerfuegbareEvents().add(verfuegbar);
        geraetRepository.save(geraet1);
        return "redirect://localhost:8080/user/myThings";
    }

    @PostMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id, @ModelAttribute(name = "notification") Notification notification,
                           Principal principal) throws Exception {
        Person anfragePerson = personRepository.findByUsername(principal.getName()).get();
        Geraet geraet = geraetRepository.findById(id).get();
        Date startDate = new Date(notification.getMietezeitpunktStart().getTime() + 60 * 60 * 6000);
        Date endDate = new Date(notification.getMietezeitpunktEnd().getTime() + 60 * 60 * 6000);
        notification.setType("request");
        notification.setAnfragePerson(anfragePerson);
        notification.setGeraet(geraet);
        notification.setMietezeitpunktStart(startDate);
        notification.setMietezeitpunktEnd(endDate);
        notification.setBesitzer(geraet.getBesitzer());
        notification.setEncode(geraet.getEncode());

        Person besitzer = geraet.getBesitzer();
        notificationRepository.save(notification);

        mailService.sendAnfragMail(besitzer, geraet, principal);

        return "redirect://localhost:8080";
    }

    @PostMapping("/sale/{id}")
    public String anfragen(@PathVariable Long id, Principal principal) throws Exception {
        Geraet geraet = geraetRepository.findById(id).get();
        Person person = geraet.getBesitzer();
        proPayService.ueberweisen(principal.getName(), person.getUsername(), geraet.getKosten());
        Person buyer = personRepository.findByUsername(principal.getName()).get();
        geraet.setBesitzer(buyer);
        geraetRepository.save(geraet);
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
                                @RequestParam(name = "files",value = "files",required = false) MultipartFile[] files, Principal principal) throws IOException {
        List<Bild> bilds = new ArrayList<>();
        personService.umwechsleMutifileZumBild(files, bilds);
        RentEvent verfuegbar = new RentEvent();
        TimeInterval timeIntervalWithout = new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd());
        TimeInterval timeInterval = geraetService.convertToCET(timeIntervalWithout);
        if (!geraet.isForsale()) {
            verfuegbar.setTimeInterval(timeInterval);
            geraet.getVerfuegbareEvents().add(verfuegbar);
        }
        geraet.setBilder(bilds);
        geraet.setLikes(0);
        Person person = personRepository.findByUsername(principal.getName()).get();
        geraet.setBesitzer(person);

        geraetRepository.save(geraet);

        return "redirect://localhost:8080/user/myThings";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        try {
            proPayService.saveAccount(person.getUsername());
            Account account = accountRepository.findByAccount(person.getUsername()).get();
            List<Transaction> transactions = proPayService.getAllPastTransactionsForPerson(person);
            model.addAttribute("transactions", transactions);
            model.addAttribute("account", account);
        }catch (Exception e){
            return "user/propayNotAvailable";
        }
        return "user/proPay";
    }

    @PostMapping("/propayErhoehung")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account,
                                 Model model){
        model.addAttribute(personRepository.findByUsername(account));
        try {
            proPayService.erhoeheAmount(account, amount);
        } catch (IOException e) {
            return "user/propayNotAvailable";
        }
        return "redirect://localhost:8080";
    }

    @GetMapping("/geraet/{id}")
    public String geraet(@PathVariable Long id, Model model, Principal principal) {
        String person = principal.getName();
        Geraet geraet = geraetRepository.findById(id).get();
        Person personForComment = geraet.getBesitzer();
        List<String> encodes = geraetService.geraetBilder(geraet);
        Account account = accountRepository.findByAccount(person).get();
        model.addAttribute("account",account);
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
        model.addAttribute("person", besitzer);
        return "user/besitzerInfo";
    }
    @GetMapping("/mieterInfo/{id}")
    public String mieterInfo(@PathVariable Long id, Model model) {
        Person mieter = personRepository.findById(id).get();
        if (mieter.getFoto().getBild().length > 0) {
            mieter.setEncode(mieter.getFoto().encodeBild());
        }

        model.addAttribute("comments", mieter.getComments());
        model.addAttribute("person", mieter);
        return "user/mieterInfo";
    }

    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        Person person = geraet.getBesitzer();

        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "user/edit";
    }

    @GetMapping("/geraet/zurueckgeben/{id}")
    public String geraetZurueck(@PathVariable Long id, Principal principal) throws Exception {
        Person person = personRepository.findByUsername(principal.getName()).get();
        RentEvent rentEvent = rentEventRepository.findById(id).get();
        rentEvent.setReturnStatus(ReturnStatus.WAITING_FOR_CONFIRMATION);
        rentEventRepository.save(rentEvent);

        Geraet geraet = rentEvent.getGeraet();

        Notification newNotification = new Notification();
        newNotification.setType("return");
        newNotification.setAnfragePerson(person);
        newNotification.setGeraet(rentEvent.getGeraet());
        newNotification.setRentEvent(rentEvent);
        newNotification.setBesitzer(geraet.getBesitzer());
        notificationRepository.save(newNotification);

        Person besitzer = geraet.getBesitzer();
        mailService.sendReturnMail(besitzer, geraet);

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
        Person mieter = notification.getAnfragePerson();
        Geraet geraet = notification.getGeraet();

        mailService.sendRefuseRequestMail(mieter, geraet);
        notificationRepository.deleteById(id);
        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id, Model model) throws Exception {

        Notification notification = notificationRepository.findById(id).get();
        Person mieter = notification.getAnfragePerson();
        Geraet geraet = notification.getGeraet();
        model.addAttribute("person", geraet.getBesitzer());
        int reservationId = 0;
        try {
            reservationId = proPayService.erzeugeReservation(mieter.getUsername(), geraet.getBesitzerUsername(), geraet.getKaution());
        } catch (IOException e) {
            return "user/propayNotAvailable";
        }

        TimeInterval timeInterval = new TimeInterval(notification.getMietezeitpunktStart(), notification.getMietezeitpunktEnd());
        RentEvent rentEvent = new RentEvent();
        rentEvent.setMieter(mieter);
        rentEvent.setTimeInterval(timeInterval);
        rentEvent.setGeraet(geraet);
        rentEvent.setReservationId(reservationId);
        rentEvent.setReturnStatus(ReturnStatus.BOOKED);
        geraet.getRentEvents().add(rentEvent);
        int index = personService.positionOfFreeBlock(geraet, rentEvent);
        personService.splitTimeIntervalsOfGeraetAvailability(geraet, index, rentEvent);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);

        Person person = mieter;
        mailService.sendAcceptRequestMail(person, geraet);

        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        RentEvent rentEvent = notification.getRentEvent();
        rentEvent.setReturnStatus(ReturnStatus.KAPUTT);
        rentEvent.setGrundForReturn(grund);
        rentEventRepository.save(rentEvent);

        Person mieter = rentEvent.getMieter();
        Geraet geraet = rentEvent.getGeraet();
        mailService.sendRefuseReturnMail(mieter, geraet);
        personService.makeComment(geraet, mieter, grund);
        notificationRepository.deleteById(id);

        return "redirect://localhost:8080/user/notifications";
    }

    @PostMapping("/notification/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id, @ModelAttribute("grund") String grund,
                                           Model model) throws Exception {
        Notification notification = notificationService.getNotificationById(id);
        RentEvent rentEvent = notification.getRentEvent();
        Geraet geraet = rentEvent.getGeraet();
        Person mieter = rentEvent.getMieter();
        mailService.sendAcceptReturnMail(mieter, geraet);
        personService.makeComment(geraet, mieter, grund);
        double amount = rentEvent.calculatePrice();
        model.addAttribute("person", geraet.getBesitzer());
        try {
            proPayService.ueberweisen(notification.getAnfragePersonUsername(), notification.getBesitzerUsername(), (int) amount);
            proPayService.releaseReservation(mieter.getUsername(), rentEvent.getReservationId());
        }catch (IOException e){
            return "user/propayNotAvailable";
        }
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);
        rentEventRepository.delete(rentEvent);
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
    public String changeProfile(Model model, @RequestParam(value = "file",required = false) MultipartFile file,
                               @ModelAttribute("person") Person p, Principal principal) throws IOException {
        model.addAttribute("person", personService.savePerson(principal, file, p));
        return "default/confirmationOfRegistration";
    }

    @GetMapping("/geraet/addLikes/{id}")
    public String like(@PathVariable Long id, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        geraetService.addLike(id, person);
        return "redirect://localhost:8080";
    }

    @PostMapping("/geraet/edit/{id}")
    public String geraetEdit(Model model, @PathVariable Long id, @ModelAttribute Geraet geraet,
                             @RequestParam(value = "files",required = false) MultipartFile[] files) throws IOException {
        geraetService.saveGeraet(files, geraet, id, false);
        List<Geraet> geraete = null;
        model.addAttribute("geraete", geraete);
        return "redirect://localhost:8080/user/myThings";
    }


    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    String permittedSizeException (Exception e){
        e.printStackTrace();
        return "<h3>The file exceeds its maximum permitted size of 15 MB. Please reload your page.</h3>" +
                "    <div>\n" +
                "            <span>\n" +
                "                <a href=\"/\">Or if you want to back to home</a>\n" +
                "            </span>\n" +
                "    </div>";
    }

    public UserController(ProPayService proPayService, AccountRepository accountRepository, GeraetService geraetService, MailService mailService, NotificationRepository notificationRepository, PersonService personService, RentEventRepository rentEventRepository, PersonRepository personRepository, GeraetRepository geraetRepository, NotificationService notificationService) {
        this.personRepository = personRepository;
        this.geraetRepository=geraetRepository;
        this.notificationService=notificationService;
        this.rentEventRepository=rentEventRepository;
        this.personService=personService;
        this.notificationRepository=notificationRepository;
        this.mailService=mailService;
        this.geraetService=geraetService;
        this.accountRepository=accountRepository;
        this.proPayService=proPayService;
    }
}
