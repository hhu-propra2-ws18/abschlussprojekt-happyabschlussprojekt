package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.GeraetService;
import com.propra.happybay.Service.UserServices.MailService;
import com.propra.happybay.Service.UserServices.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private GeraetMitReservationIDRepository geraetMitReservationIDRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;

    @Autowired
    private RentEventRepository rentEventRepository;

    @Autowired
    private GeraetService geraetService;

    public UserController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        if(person.getFoto().getBild().length>0){
            person.setEncode(person.getFoto().encodeBild());
        }

        model.addAttribute("person", person);
        if (name.equals("admin")) { return "redirect:/admin/"; }
        else { return "user/profile"; }
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        model.addAttribute("geraete",geraetService.getAllByBesitzerWithBilder(name));
        return "user/myThings";
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal) {
        String mieterName = principal.getName();
        Person person = personRepository.findByUsername(mieterName).get();
        model.addAttribute("person", person);
        model.addAttribute("geraete", geraetService.getAllByMieterWithBilder(mieterName));
        return "user/rentThings";
    }

    @GetMapping("/notifications")
    public String makeNotifications(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);

        List<Notification> notifications = notificationRepository.findAllByBesitzer(name);
        System.out.println(notifications);
        for (Notification notification : notifications) {
            if (geraetRepository.
                    findById(notification.getGeraetId())
                    .get()
                    .getBilder()
                    .get(0)
                    .getBild()
                    .length > 0) {
                notification.setEncode(geraetRepository.findById(notification.getGeraetId()).get().getBilder().get(0).encodeBild());
            }
        }
        model.addAttribute("notification", notifications);

        List<TransferRequest> transferRequestList=transferRequestRepository.findAll();
        model.addAttribute("transferRequestList",transferRequestList);

        return "user/notifications";
    }

    @GetMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id,Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        Geraet geraet1 = geraetRepository.findById(id).get();

        model.addAttribute("geraet",geraet1);
        model.addAttribute("notification", new Notification());
        return "user/anfragen";
    }
    @PostMapping("/anfragen/{id}")
    public String anfragen(Model model, @PathVariable Long id, @ModelAttribute Notification notification, Principal principal) throws Exception {

        Notification newNotification = new Notification();
        newNotification.setType("request");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setMessage(notification.getMessage());
        newNotification.setZeitraum(notification.getZeitraum());
        newNotification.setMietezeitpunktStart(notification.getMietezeitpunktStart());
        newNotification.setMietezeitpunktEnd(notification.getMietezeitpunktEnd());
        newNotification.setBesitzer(notification.getBesitzer());


        Geraet geraet = geraetRepository.findById(newNotification.getGeraetId()).get();
        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();
        geraet.setZeitraum(notification.getZeitraum());

        newNotification.setEncode(geraet.getEncode());
        notificationRepository.save(newNotification);

        mailService.sendAnfragMail(person, geraet, principal);

        return "redirect:/";
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
        verfuegbar.setTimeInterval(new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd()));
        geraet.setBilder(bilds);
        geraet.setVerfuegbar(true);
        geraet.setLikes(0);
        geraet.setBesitzer(principal.getName());

        Person person=personRepository.findByUsername(principal.getName()).get();
        int aktionPunkte=person.getAktionPunkte();
        person.setAktionPunkte(aktionPunkte+10);
        geraetRepository.save(geraet);

        geraet.getVerfuegbareEvents().add(verfuegbar);
        geraetRepository.save(geraet);

        return "redirect:/user/myThings";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        proPayService.saveAccount(person.getUsername());
        model.addAttribute("person", person);
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        model.addAttribute("transferRequest",new TransferRequest());
        return "user/proPay";
    }
    @PostMapping("/propay")
    public String propay(Principal principal, @ModelAttribute("transferRequest") TransferRequest transferRequest){
        transferRequest.setUsername(principal.getName());
        transferRequestRepository.save(transferRequest);
        return "redirect:/";
    }
    @GetMapping("/geraet/{id}")
    public String geraet(@PathVariable Long id, Model model, Principal principal) {
        String person = principal.getName();
        Geraet geraet = geraetRepository.findById(id).get();
        //创建personInfo 为了comment
        Person personForComment = personRepository.findByUsername(geraet.getBesitzer()).get();
        List<String> encodes = geraetService.geraetBilder(geraet);
        model.addAttribute("encodes",encodes);
        model.addAttribute("person", personRepository.findByUsername(person).get());
        model.addAttribute("geraet", geraet);
        model.addAttribute("personForComment",personForComment);
        return "user/geraet";
    }

    //This is comment block
    @GetMapping("/BesitzerInfo/{id}")
    public String besitzerInfo(@PathVariable Long id, Model model){
        Person besitzer=personRepository.findById(id).get();
        besitzer.setEncode(encodeBild(besitzer.getFoto()));

        model.addAttribute("comments",besitzer.getComments());
        model.addAttribute("person",besitzer);
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
    public String geraetZurueck(@PathVariable Long id, Model model, Principal principal) throws Exception {
        Geraet geraet = geraetRepository.findById(id).get();

        geraet.setReturnStatus(ReturnStatus.WAITING);
        geraet.setZeitraum(-1);
        geraetRepository.save(geraet);

        Notification newNotification = new Notification();
        newNotification.setType("return");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setBesitzer(geraet.getBesitzer());
        newNotification.setZeitraum(geraet.getZeitraum());
        notificationRepository.save(newNotification);

        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();
        mailService.sendReturnMail(person, geraet);

        return "redirect:/user/rentThings";
    }

    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect:/user/myThings";
    }
    @PostMapping("/notification/refuseRequest/{id}")
    public String notificationRefuseRequest(@PathVariable Long id) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        String mieter = notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();

        Person person = personRepository.findByUsername(mieter).get();

        mailService.sendRefuseRequestMail(person, geraet);
        notificationRepository.deleteById(id);
        return "redirect:/user/notifications";
    }
    @PostMapping("/notification/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id, Principal principal) throws Exception {

        Notification notification = notificationRepository.findById(id).get();
        String mieter = notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setVerfuegbar(false);
        geraet.setMieter(mieter);
        geraet.setZeitraum(notification.getZeitraum());
//        LocalDate endzeit = notification.getMietezeitPunkt().toLocalDate().plusDays(notification.getZeitraum());
//        geraet.setEndzeitpunkt(endzeit);
//        geraetRepository.save(geraet);

        RentEvent rentEvent = new RentEvent();
        rentEvent.setMieter(mieter);
        rentEvent.setTimeInterval(new TimeInterval(notification.getMietezeitpunktStart(), notification.getMietezeitpunktEnd()));
        geraet.getRentEvents().add(rentEvent);
        int index = userService.positionOfFreeBlock(geraet, rentEvent);
        userService.intervalZerlegen(geraet, index, rentEvent);
        geraetRepository.save(geraet);

        notificationRepository.deleteById(id);
        int reservationId = proPayService.erzeugeReservation(mieter, geraet.getBesitzer(), (int) geraet.getKaution());
        GeraetMitReservationID geraetMitReservationID = new GeraetMitReservationID();
        geraetMitReservationID.setGeraetID(geraet.getId());
        geraetMitReservationID.setReservationID(reservationId);
        geraetMitReservationIDRepository.save(geraetMitReservationID);

        Person person = personRepository.findByUsername(mieter).get();
        mailService.sendAcceptRequestMail(person, geraet);

        return "redirect:/user/notifications";
    }

    @PostMapping("/notification/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification=notificationRepository.findById(id).get();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setGrundReturn(grund);
        geraet.setReturnStatus(ReturnStatus.KAPUTT);
        geraetRepository.save(geraet);
        Person person = personRepository.findByUsername(geraet.getMieter()).get();
        mailService.sendRefuseReturnMail(person, geraet);

        //从这里改了comment
        Comment comment = new Comment();
        comment.setDate(LocalDate.now());
        comment.setGeraetTitel(geraet.getTitel());
        comment.setMessage(grund);
        comment.setSenderFrom(personRepository.findByUsername(geraet.getBesitzer()).get().getUsername());
        comment.setPersonId(personRepository.findByUsername(geraet.getBesitzer()).get().getId());
        person.getComments().add(comment);
        personRepository.save(person);
        //

        notificationRepository.deleteById(id);
        return "redirect:/user/notifications";
    }

    @PostMapping("/notification/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification = notificationRepository.findById(id).get();

        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();

        Person person = personRepository.findByUsername(geraet.getMieter()).get();
        mailService.sendAcceptReturnMail(person, geraet);

        //从这里改了comment
        Comment comment = new Comment();
        comment.setDate(LocalDate.now());
        comment.setGeraetTitel(geraet.getTitel());
        comment.setMessage(grund);
        comment.setSenderFrom(personRepository.findByUsername(geraet.getBesitzer()).get().getUsername());
        comment.setPersonId(personRepository.findByUsername(geraet.getBesitzer()).get().getId());
        person.getComments().add(comment);
        personRepository.save(person);
        //这里结束

        geraet.setVerfuegbar(true);
        geraet.setReturnStatus(ReturnStatus.DEFAULT);
        geraet.setMieter(null);
        geraetRepository.save(geraet);
        double amount = 3;//eraet.getZeitraum()*geraet.getKosten();
        proPayService.ueberweisen(notification.getAnfragePerson(), notification.getBesitzer(), (int) amount);

        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraet.getId());
        proPayService.releaseReservation(notification.getAnfragePerson(),geraetMitReservationID.getReservationID());
        notificationRepository.deleteById(id);
        geraetMitReservationIDRepository.deleteById(geraetMitReservationID.getReservationID());
        return "redirect:/user/notifications";
    }
    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal){
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

        geraetRepository.save(geraet1);
        List<Geraet> geraete = null;//personRepository.findByUsername(person.getName()).get().getMyThings();
        model.addAttribute("geraete", geraete);
        return "redirect:/user/myThings";
    }

    @GetMapping("/bezahlen/{id}")
    public String bezahlen(Model model,@ModelAttribute Geraet geraet, Principal person, @PathVariable Long id) {
        Geraet geraet1 = geraetRepository.findById(id).get();
        String mieterName= person.getName();
        geraet1.setMieter(mieterName);
        geraetRepository.save(geraet1);
        return "user/confirmBezahlen";
    }

    @GetMapping("/aboutUs")
    public String about(Model model, Principal principal){
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                model.addAttribute("person", personRepository.findByUsername(name).get());
            }
        }
        return "default/aboutUs";
    }

    @GetMapping("/geraet/addLikes/{id}")
    public String like(@PathVariable Long id) {
        Geraet geraet = geraetRepository.findById(id).get();
        geraet.setLikes(geraet.getLikes() + 1);
        geraetRepository.save(geraet);
        return "redirect:/";
    }

    //delate after
    public String encodeBild(Bild bild) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }
}
