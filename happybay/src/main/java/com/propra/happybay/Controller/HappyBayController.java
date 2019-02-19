package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class HappyBayController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private GeraetMitReservationIDRepository geraetMitReservationIDRepository;


    @GetMapping("/")
    public String index(Model model, Principal principal){
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                List<Notification> notifications = notificationRepository.findAllByBesitzer(name);
                Person person = personRepository.findByUsername(name).get();
                person.setAnzahlNotifications(notifications.size());
                personRepository.save(person);
                model.addAttribute("person", person);
            }
            else {
                model.addAttribute("person", new Person());
            }
        }
        List<Geraet> geraete = geraetRepository.findAll();
        for (Geraet geraet: geraete){
            geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
        }
        model.addAttribute("geraete", geraete);
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/personInfo")
    public String personInfo(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        return "profile";
    }

    @PostMapping("/addNewUser")
    public String addToDatabase(@RequestParam("file") MultipartFile file,
                                @ModelAttribute("person") Person person, BindingResult bindingResult,
                                Model model) throws IOException {
        userValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            for (int i=0; i< bindingResult.getAllErrors().size(); i++){
                errorList.add(bindingResult.getAllErrors().get(i).getCode());
            }
            System.out.println(errorList);
            model.addAttribute("errorList", errorList);
            return "register";
        }
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setRole("ROLE_USER");
        person.setPassword(encoder.encode(person.getPassword()));
        personRepository.save(person);
        proPayService.saveAccount(person.getUsername());
        person.setPassword("");
        model.addAttribute("person", person);
        return "confirmationOfRegistration";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        person.setEncode(encodeBild(person.getFoto()));

        model.addAttribute("user", person);
        return "profile";
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);

        List<Geraet> geraets = geraetRepository.findAllByBesitzer(name);
        for (Geraet geraet: geraets){
            geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
        }
        model.addAttribute("geraete",geraets);
        return "myThings";
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal) {
        String mieterName = principal.getName();
        Person person = personRepository.findByUsername(mieterName).get();
        List<Geraet> geraete = geraetRepository.findAllByMieter(mieterName);
        model.addAttribute("person", person);
        model.addAttribute("geraete", geraete);
        return "rentThings";
    }

    @GetMapping("/user/notifications")
    public String makeNotifications(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);

        List<Notification> notifications = notificationRepository.findAllByBesitzer(name);
        model.addAttribute("notification", notifications);
        return "notifications";
    }

    @GetMapping("/user/anfragen/{id}")
    public String anfragen(@PathVariable Long id,Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        Geraet geraet1 = geraetRepository.findById(id).get();

        model.addAttribute("geraet",geraet1);
        model.addAttribute("notification", new Notification());
        return "anfragen";
    }
    @PostMapping("/user/anfragen/{id}")
    public String anfragen(Model model,@PathVariable Long id, @ModelAttribute Notification notification, Principal principal) {

        Notification newNotification = new Notification();
        newNotification.setType("request");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setMessage(notification.getMessage());
        newNotification.setZeitraum(notification.getZeitraum());
        newNotification.setMietezeitPunkt(notification.getMietezeitPunkt());
        newNotification.setBesitzer(notification.getBesitzer());
        notificationRepository.save(newNotification);

        Geraet geraet = geraetRepository.findById(newNotification.getGeraetId()).get();
        geraet.setMietezeitpunkt(notification.getMietezeitPunkt());
        geraet.setZeitraum(notification.getZeitraum());

        return "redirect:/";
    }
    @GetMapping("/addGeraet")
    public String addGeraet(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        return "addGeraet";
    }
    @PostMapping("/addGeraet")
    public String confirmGeraet(@ModelAttribute("geraet") Geraet geraet,
                                @RequestParam("files") MultipartFile[] files, Principal person) throws IOException {
        List<Bild> bilds = new ArrayList<>();
        for (MultipartFile file : files) {
            Bild bild = new Bild();
            bild.setBild(file.getBytes());
            bilds.add(bild);
        }
        geraet.setBilder(bilds);
        geraet.setVerfuegbar(true);
        geraet.setLikes(0);
        geraet.setBesitzer(person.getName());
        geraetRepository.save(geraet);
        return "redirect:/myThings";
    }



    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Ihr Benutzername oder Kennwort sind nicht gültig.");

        if (logout != null)
            model.addAttribute("message", "Sie wurden erfolgreich abgemeldet.");
        return "login";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        proPayService.saveAccount(person.getUsername());
        model.addAttribute("person", person);
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "proPay";
    }

    @GetMapping("/geraet/{id}")
    public String geraet(@PathVariable Long id, Model model, Principal principal) {
        String person = principal.getName();
        Geraet geraet = geraetRepository.findById(id).get();
        List<Bild> bilds = geraet.getBilder();
        List<String> encodes = new ArrayList<>();
        for(int i=1;i<bilds.size();i++){
            encodes.add(encodeBild(bilds.get(i)));
        }
        geraet.setEncode(encodeBild(bilds.get(0)));
        model.addAttribute("encodes",encodes);
        //model.addAttribute("person", principal);
        model.addAttribute("person", personRepository.findByUsername(person).get());
        model.addAttribute("geraet", geraet);
        return "geraet";
    }

    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Person person = personRepository.findByUsername(geraetRepository.findById(id).get().getBesitzer()).get();
        person.setEncode(encodeBild(person.getFoto()));

        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "edit";
    }

    @GetMapping("/geraet/zurueckgeben/{id}")
    public String geraetZurueck(@PathVariable Long id, Model model,Principal principal) {
        Geraet geraet = geraetRepository.findById(id).get();

        geraet.setReturnStatus("waiting");
        geraetRepository.save(geraet);

        Notification newNotification = new Notification();
        newNotification.setType("return");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setBesitzer(geraet.getBesitzer());
        notificationRepository.save(newNotification);

        return "redirect:/rentThings";
    }
    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect:/myThings";
    }
    @PostMapping("/notification/refuseRequest/{id}")
    public String notificationRefuseRequest(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return "redirect:/user/notifications";
    }
    @PostMapping("/notification/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id,Principal principal) throws IOException {
        Notification notification = notificationRepository.findById(id).get();
        String mieter = notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setVerfuegbar(false);
        geraet.setMieter(mieter);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);
        int reservationId = proPayService.erzeugeReservation(mieter, geraet.getBesitzer(), geraet.getKaution());
        GeraetMitReservationID geraetMitReservationID = new GeraetMitReservationID(reservationId,geraet.getId());
        geraetMitReservationIDRepository.save(geraetMitReservationID);
        return "redirect:/user/notifications";
    }

    @PostMapping("/notification/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id) {
        Notification notification=notificationRepository.findById(id).get();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setReturnStatus("kaputt");
        geraetRepository.save(geraet);

        notificationRepository.deleteById(id);
        return "redirect:/user/notifications";
    }
    @PostMapping("/notification/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id) throws IOException {
        Notification notification = notificationRepository.findById(id).get();

        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setVerfuegbar(true);
        geraet.setReturnStatus("default");
        geraet.setMieter(null);
        geraetRepository.save(geraet);
        double amount = notification.getZeitraum()*geraet.getKosten();
        //bezahlen und Reservierung aufheben
        //int index =
        proPayService.ueberweisen(notification.getAnfragePerson(),notification.getBesitzer(),amount);
        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraet.getId());
        //proPayService.releaseReservation(notification.getAnfragePerson(),);
        notificationRepository.deleteById(id);
        return "redirect:/user/notifications";
    }
    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal){
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
        return "changeProfile";
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
        return "confirmationOfRegistration";
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
        return "redirect:/myThings";
    }

    @GetMapping("/user/bezahlen/{id}")
    public String bezahlen(Model model,@ModelAttribute Geraet geraet, Principal person, @PathVariable Long id) {
        Geraet geraet1 = geraetRepository.findById(id).get();
        String mieterName= person.getName();
        geraet1.setMieter(mieterName);
        geraetRepository.save(geraet1);
        return "confirmBezahlen";
    }

    @PostMapping("/erhoeheAmount")
    public String erhoeheAmount(Model model, @ModelAttribute("username") String username) throws IOException {
        Person person = personRepository.findByUsername(username).get();
        model.addAttribute("person", person);
        proPayService.erhoeheAmount(person.getUsername(), 10);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "redirect:/admin";
    }

    //@GetMapping("/ueberweisen")
    //public String ueberweisen(Model model, Principal principal) throws IOException {
    //    String name = principal.getName();
    //    Person person = personRepository.findByUsername(name).get();
    //    model.addAttribute("user", person);
    //    proPayService.ueberweisen(person.getUsername(), "ancao100", 10);
    //    proPayService.saveAccount(person.getUsername());
    //    Account account = accountRepository.findByAccount(person.getUsername()).get();
    //    model.addAttribute("account", account);
    //    return "proPay";
    //}
    @GetMapping("/aboutUs")
    public String about(Model model, Principal principal){
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                model.addAttribute("person", personRepository.findByUsername(name).get());
            }
        }
        return "aboutUs";
    }

    @GetMapping("/admin")
    public String adminFunktion(Model model, Principal principal){
        List<PersonMitAccount> personenMitAccounts = new ArrayList<>();
        List<Person> personList = personRepository.findAll();
        for (Person person : personList) {
            if (!person.getUsername().equals("admin")) {
                Optional<Account> account = accountRepository.findByAccount(person.getUsername());
                personenMitAccounts.add(new PersonMitAccount(person, account.get()));
            }
        }
        model.addAttribute("personenMitAccounts",personenMitAccounts);
        List<Geraet> geraeteMitKonflikten = geraetRepository.findAllByReturnStatus("kaputt");
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person",person);
        model.addAttribute("geraeteMitKonflikten", geraeteMitKonflikten);
        return "admin";
    }

    @GetMapping("/geraet/addLikes/{id}")
    public String like(@PathVariable Long id) {
        Geraet geraet = geraetRepository.findById(id).get();
        geraet.setLikes(geraet.getLikes() + 1);
        geraetRepository.save(geraet);
        return "redirect:/";
    }

    private String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }


}
