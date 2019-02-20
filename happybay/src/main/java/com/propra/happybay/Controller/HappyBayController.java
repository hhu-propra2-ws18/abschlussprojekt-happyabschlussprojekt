package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.Service.MailService;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserValidator;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Controller
public class HappyBayController {
    private int zahl;
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
    private JavaMailSender sender;
    @Autowired
    private MailService mailService;



    @GetMapping("/")
    public String index(Model model, Principal principal,@RequestParam(value="key",required = false,defaultValue = "") String key){
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                model.addAttribute("person", personRepository.findByUsername(name).get());

                List<Geraet> rentThings = geraetRepository.findAllByMieter(name);
                List<Geraet> remindRentThings=new ArrayList<>();
                List<Geraet> overTimeThings=new ArrayList<>();
                LocalDate deadLine = LocalDate.now().plusDays(3);
                for(Geraet geraet: rentThings){
                    if(geraet.getEndzeitpunkt().isBefore(deadLine)||geraet.getEndzeitpunkt().isEqual(deadLine)){
                        if(LocalDate.now().isAfter(geraet.getEndzeitpunkt())){
                            overTimeThings.add(geraet);
                        }else{
                            remindRentThings.add(geraet);
                        }
                    }
                }
                model.addAttribute("remindRentThings",remindRentThings);
                model.addAttribute("overTimeThings",overTimeThings);
            }
        }
        List<Geraet> geraete = geraetRepository.findAllByTitelLike("%"+key+"%");
        System.out.println(key+geraete);
        //List<Geraet> geraete = geraetRepository.findAll();
        for (Geraet geraet: geraete){
            if(geraet.getBilder().size()==0){
                geraet.setBilder(null);
            }
            if(geraet.getBilder()!=null && geraet.getBilder().size()>0){
                geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
            }

        }
        model.addAttribute("geraete", geraete);
        model.addAttribute("zahl",zahl);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/add")
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
            return "addUser";
        }
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setRole("ROLE_USER");
        person.setPassword(encoder.encode(person.getPassword()));
        personRepository.save(person);
        person.setPassword("");
        model.addAttribute("person", person);
        return "confirmationAdd";
    }

    @GetMapping("/admin")
    public String administrator() {
        return "admin";
    }




    @GetMapping("/personInfo")
    public String person(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();

        List<Geraet> geraete=geraetRepository.findAllByBesitzer(name);
        zahl=0;
        for (Geraet geraet:geraete){
            zahl+=notificationRepository.findByGeraetId(geraet.getId()).size();
        }

        model.addAttribute("user", person);
        model.addAttribute("zahl",zahl);
        return "personInfo";
    }


    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        person.setEncode(encodeBild(person.getFoto()));


        model.addAttribute("zahl",zahl);
        model.addAttribute("user", person);
        return "profile";
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();

        model.addAttribute("user", person);

        List<Geraet> geraets = geraetRepository.findAllByBesitzer(name);
        for (Geraet geraet: geraets){
            geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
        }
        model.addAttribute("geraete",geraets);
        model.addAttribute("zahl",zahl);
        return "myThings";
    }

    private String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal) {
        String mieterName = principal.getName();
        Person person = personRepository.findByUsername(mieterName).get();
        List<Geraet> geraetes=geraetRepository.findAllByMieter(mieterName);
        for (Geraet geraet: geraetes){
            geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
        }
        model.addAttribute("user",person);
        model.addAttribute("geraete", geraetes);
        model.addAttribute("zahl",zahl);
        return "rentThings";
    }

    @GetMapping("/user/myRemind")
    public String myRemind(Model model, Principal principal) {
        List<Geraet> geraetList=geraetRepository.findAllByBesitzer(principal.getName());
        List<Notification> newNotification=new ArrayList<>();

        for(Geraet geraet:geraetList){
            List<Notification> notificationList=notificationRepository.findAllByGeraetId(geraet.getId());
            for(Notification notification:notificationList){
                    notification.setEncode(encodeBild(geraet.getBilder().get(0)));
                    newNotification.add(notification);
            }

        }
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("");
        model.addAttribute("user", person);
        model.addAttribute("notification",newNotification);
        return "myRemind";
    }
    @GetMapping("/user/anfragen/{id}")
    public String anfragen(@PathVariable Long id,Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        Geraet geraet1 = geraetRepository.findById(id).get();

        model.addAttribute("geraet",geraet1);
        model.addAttribute("notification",new Notification());
        return "anfragen";
    }
    @PostMapping("/user/anfragen/{id}")
    public String anfragen(Model model,@PathVariable Long id,
                           @ModelAttribute Notification notification, Principal principal) throws Exception{

        Notification newNotification=new Notification();
        newNotification.setType("request");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setMessage(notification.getMessage());
        newNotification.setZeitraum(notification.getZeitraum());
        newNotification.setMietezeitPunkt(notification.getMietezeitPunkt());

        Geraet geraet=geraetRepository.findById(newNotification.getGeraetId()).get();
        newNotification.setEncode(geraet.getEncode());
        geraet.setReturnStatus("default");
        notificationRepository.save(newNotification);

        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();

        mailService.sendAnfragMail(person,geraet,principal);

        return "redirect:/";
    }
    @GetMapping("/addGeraet")
    public String addGeraet(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
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

        geraet.setBesitzer(person.getName());
        geraetRepository.save(geraet);
        return "redirect:/myThings";
    }



    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }

    @GetMapping("/proPay")
    public String proPay(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        model.addAttribute("zahl",zahl);
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
        model.addAttribute("person", principal);
        model.addAttribute("user", personRepository.findByUsername(person).get());
        model.addAttribute("geraet", geraet);
        return "geraet";
    }

    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("geraet", geraet);
        return "edit";
    }
    @GetMapping("/geraet/zurueckgeben/{id}")
    public String geraetZurueck(@PathVariable Long id, Model model,Principal principal) throws Exception{
        Geraet geraet = geraetRepository.findById(id).get();
        geraet.setZeitraum(-1);
        geraet.setReturnStatus("waiting");
        geraetRepository.save(geraet);

        Notification newNotification=new Notification();
        newNotification.setType("return");
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        notificationRepository.save(newNotification);

        Person person = personRepository.findByUsername(geraet.getBesitzer()).get();

        mailService.sendReturnMail(person,geraet);

        return "redirect:/rentThings";
    }
    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect:/myThings";
    }
    @PostMapping("/notification/refuseRequest/{id}")
    public String notificationRefuseRequest(@PathVariable Long id) throws Exception{
        Notification notification=notificationRepository.findById(id).get();
        String mieter=notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();

        Person person = personRepository.findByUsername(mieter).get();

        mailService.sendRefuseRequestMail(person,geraet);

        notificationRepository.deleteById(id);
        return "redirect:/user/myRemind";
    }

    @PostMapping("/notification/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id) throws Exception{
        Notification notification=notificationRepository.findById(id).get();
        String mieter=notification.getAnfragePerson();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setVerfuegbar(false);
        geraet.setMieter(mieter);
        geraet.setZeitraum(notification.getZeitraum());

        Person person = personRepository.findByUsername(mieter).get();

        mailService.sendAcceptRequestMail(person,geraet);

        LocalDate endzeit = notification.getMietezeitPunkt().toLocalDate().plusDays(notification.getZeitraum());
        geraet.setEndzeitpunkt(endzeit);
        geraetRepository.save(geraet);

        notificationRepository.deleteById(id);
        return "redirect:/user/myRemind";
    }

    @PostMapping("/notification/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id) throws Exception{
        Notification notification=notificationRepository.findById(id).get();
        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setReturnStatus("kaputt");
        geraetRepository.save(geraet);

        Person person = personRepository.findByUsername(geraet.getMieter()).get();

        mailService.sendRefuseReturnMail(person,geraet);
        return "redirect:/user/myRemind";
    }
    @PostMapping("/notification/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id) throws Exception{
        Notification notification=notificationRepository.findById(id).get();

        Geraet geraet = geraetRepository.findById(notification.getGeraetId()).get();

        Person person = personRepository.findByUsername(geraet.getMieter()).get();

        mailService.sendAcceptReturnMail(person,geraet);

        geraet.setMieter(null);
        geraet.setVerfuegbar(true);
        geraet.setReturnStatus("good");
        geraetRepository.save(geraet);

        return "redirect:/user/myRemind";
    }
    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal){
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        return "changeProfile";
    }

    @PostMapping("/PersonInfo/Profile/ChangeProfile")
    public String chageProfile(@RequestParam("file") MultipartFile file,
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
        System.out.println(person.getUsername() + ' ' + p.getUsername());
        personRepository.save(person);
        return "confirmationAdd";
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
    @GetMapping("/erhoeheAmount")
    public String erhoeheAmount(Model model, Principal principal) throws IOException {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        proPayService.erhoeheAmount(person.getUsername(), 10);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "proPay";
    }
    @GetMapping("/ueberweisen")
    public String ueberweisen(Model model, Principal principal) throws IOException {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        proPayService.ueberweisen(person.getUsername(), "ancao100", 10);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "proPay";
    }
    @GetMapping("/about")
    public String about(Model model, Principal principal){
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                model.addAttribute("person", personRepository.findByUsername(name).get());
            }
        }
        return "about";
    }
    @GetMapping("/erzeugeReservation")
    public String erzeugeReservation(Model model, Principal principal) throws IOException {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        proPayService.erzeugeReservation(person.getUsername(),"ancao100", 4);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "proPay";
    }


}
