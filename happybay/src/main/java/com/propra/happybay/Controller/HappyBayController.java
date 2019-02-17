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

import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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


    @GetMapping("/")
    public String index(Model model) {
        List<Geraet> geraete = geraetRepository.findAll();
        for (Geraet geraet: geraete){
            geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
        }
        model.addAttribute("geraete", geraete);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/add")
    public String addToDatabase(@RequestParam("file") MultipartFile file,
                                @ModelAttribute("person") Person person, BindingResult bindingResult,
                                Model model) throws IOException{
        userValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            for (int i = 0; i < bindingResult.getAllErrors().size(); i++) {
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
        List<Geraet> geraete=geraetRepository.findAllByMieter(mieterName);
        model.addAttribute("geraete", geraete);
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
                    newNotification.add(notification);
            }

        }



        model.addAttribute("notification",newNotification);
        return "myRemind";
    }
    @GetMapping("/user/anfragen/{id}")
    public String anfragen(@PathVariable Long id,Model model) {
        Geraet geraet1 = geraetRepository.findById(id).get();

        model.addAttribute("geraet",geraet1);
        model.addAttribute("notification",new Notification());
        return "anfragen";
    }
    @PostMapping("/user/anfragen/{id}")
    public String anfragen(Model model,@PathVariable Long id, @ModelAttribute Notification notification, Principal principal) {

        Notification newNotification=new Notification();
        newNotification.setAnfragePerson(principal.getName());
        newNotification.setGeraetId(id);
        newNotification.setMessage(notification.getMessage());
        newNotification.setZeitraum(notification.getZeitraum());
        newNotification.setMietezeitPunkt(notification.getMietezeitPunkt());

        Geraet geraet=geraetRepository.findById(newNotification.getGeraetId()).get();
        geraet.setMietezeitpunkt(notification.getMietezeitPunkt());
        geraet.setZeitraum(notification.getZeitraum());

        notificationRepository.save(newNotification);


        return "redirect:/";
    }
    @GetMapping("/addGeraet")
    public String addGeraet() {
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
    public String geraet(@PathVariable Long id, Model model, Principal person) {
        Geraet geraet = geraetRepository.findById(id).get();
        List<Bild> bilds = geraet.getBilder();
        List<String> encodes = new ArrayList<>();
        for(int i=1;i<bilds.size();i++){
            encodes.add(encodeBild(bilds.get(i)));
        }
        geraet.setEncode(encodeBild(bilds.get(0)));
        model.addAttribute("encodes",encodes);
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "geraet";
    }

    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("geraet", geraet);
        return "edit";
    }

    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect:/myThings";
    }
    @PostMapping("/notification/delete/{id}")
    public String notificationDelete(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return "redirect:/user/myRemind";
    }
    @PostMapping("/notification/accept/{id}")
    public String notificationAccept(@PathVariable Long id) {
        Notification notification=notificationRepository.findById(id).get();
        String mieter=notification.getAnfragePerson();
        Geraet geraet=geraetRepository.findById(notification.getGeraetId()).get();
        geraet.setVerfuegbar(false);
        geraet.setMieter(mieter);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);

        return "redirect:/user/myRemind";
    }

    @GetMapping("/PersonInfo/Profile/ChangeProfile")
    public String changeImg(Model model, Principal principal){
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("person", person);
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
}
