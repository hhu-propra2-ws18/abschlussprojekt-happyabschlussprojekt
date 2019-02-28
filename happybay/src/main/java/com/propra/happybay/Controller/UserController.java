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
        personService.checkPhoto(person);
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

    @PostMapping("/anfragen/{id}")
    public String anfragen(@PathVariable Long id, @ModelAttribute(name = "notification") Notification notification,
                           Principal principal) throws Exception {
        Person anfragePerson = personService.findByPrincipal(principal);
        Geraet geraet = geraetRepository.findById(id).get();
        notificationService.copyAndEditNotification(anfragePerson, geraet, notification, "request");
        
        Person besitzer = geraet.getBesitzer();
        mailService.sendAnfragMail(besitzer, geraet, principal);
        return "redirect://localhost:8080";
    }

    @PostMapping("/sale/{id}")
    public String anfragen(@PathVariable Long id, Principal principal) throws Exception {
        Geraet geraet = geraetRepository.findById(id).get();
        Person buyer = personService.findByPrincipal(principal);
        Person besitzer = geraet.getBesitzer();
        proPayService.ueberweisen(principal.getName(), besitzer.getUsername(), geraet.getKosten());
        geraet.setBesitzer(buyer);
        geraetRepository.save(geraet);
        mailService.sendAnfragMail(besitzer, geraet, principal);
        return "redirect://localhost:8080";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
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
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account, Model model){
        model.addAttribute(personRepository.findByUsername(account));
        try {
            proPayService.erhoeheAmount(account, amount);
        } catch (IOException e) {
            return "user/propayNotAvailable";
        }
        return "redirect://localhost:8080";
    }

    @GetMapping("/BesitzerInfo/{id}")
    public String besitzerInfo(@PathVariable Long id, Model model) {
        Person besitzer = personRepository.findById(id).get();
        personService.checkPhoto(besitzer);
        model.addAttribute("person", besitzer);
        return "user/besitzerInfo";
    }
    @GetMapping("/mieterInfo/{id}")
    public String mieterInfo(@PathVariable Long id, Model model) {
        Person mieter = personRepository.findById(id).get();
        personService.checkPhoto(mieter);
        model.addAttribute("comments", mieter.getComments());
        model.addAttribute("person", mieter);
        return "user/mieterInfo";
    }

    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        model.addAttribute("person", person);
        return "user/changeProfile";
    }

    @PostMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeProfile(Model model, @RequestParam(value = "file",required = false) MultipartFile file,
                               @ModelAttribute("person") Person p, Principal principal) throws IOException {
        model.addAttribute("person", personService.savePerson(principal, file, p));
        return "default/confirmationOfRegistration";
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

    public UserController(RentEventService rentEventService, ProPayService proPayService, AccountRepository accountRepository, GeraetService geraetService, MailService mailService, NotificationRepository notificationRepository, PersonService personService, RentEventRepository rentEventRepository, PersonRepository personRepository, GeraetRepository geraetRepository, NotificationService notificationService) {
        this.rentEventService=rentEventService;
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
