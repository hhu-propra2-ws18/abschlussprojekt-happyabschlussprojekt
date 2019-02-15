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
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
    private TransferRepository transferRepository;
    @Autowired
    private BildRepository bildRepository;

    @GetMapping("/")
    public String index(Model model){
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/add")
    public String addToDatabase(@ModelAttribute("person")Person person, BindingResult bindingResult,
                                Model model) {
        userValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            for (int i=0; i< bindingResult.getAllErrors().size(); i++){
                errorList.add(bindingResult.getAllErrors().get(i).getCode());
            }
            System.out.println(errorList);
            model.addAttribute("errorList",errorList);
            return "addUser";
        }
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

    @GetMapping("/user")
    public String user(Model m, Principal person) {
        m.addAttribute("username", person.getName());
        return "profile";
    }


    @GetMapping("/personInfo")
    public String person(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        return "personInfo";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        return "profile";
    }

    @GetMapping("/profile/ChangeProfile")
    public String changeImg(Model model, Principal principal) {
        return "changeProfile";
    }

    @GetMapping("/myThings")
    public String myThings(Model model, Principal principal){
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();

        model.addAttribute("user", person);

        model.addAttribute("geraete",geraetRepository.findAllByBesitzer(name));

        return "myThings";
    }

    @GetMapping("/rentThings")
    public String rentThings(Model model, Principal principal){
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        return "rentThings";
    }

    @GetMapping("myRemind")
    public String myRemind(Model model, Principal principal) {
        String name = principal.getName();
        Person person = personRepository.findByUsername(name).get();
        model.addAttribute("user", person);
        return "myRemind";
    }

    @GetMapping("/addGeraet")
    public String addGeraet() {
        return "addGeraet";
    }

    @PostMapping("/addGeraet")
    public String confirmGeraet(@RequestParam("files") MultipartFile[] files,
                                @ModelAttribute("geraet")Geraet geraet,Principal person) throws IOException {
        bildRepository.deleteAll();

        for (MultipartFile file : files) {
            Bild bild = new Bild();
            bild.setBild(file.getBytes());
            bildRepository.save(bild);
        }

        List<Bild> bilds = new ArrayList<>();
        bildRepository.findAll().forEach(e -> bilds.add(e));
        geraet.setBilder(bilds);

        geraet.setVerfuegbar(true);
        geraet.setBesitzer(person.getName());
        geraetRepository.save(geraet);
                return "index";
    }


    private String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout){
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
        return "proPay";
    }
    @GetMapping("/geraet/{id}")
    public String geraet(@PathVariable Long id, Model model) {
        Geraet geraet=geraetRepository.findById(id).get();
        model.addAttribute("geraet",geraet);
        return "geraet";
    }
    @GetMapping("/geraet/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Geraet geraet=geraetRepository.findById(id).get();
        model.addAttribute("geraet",geraet);
        return "edit";
    }
    @PostMapping("/geraet/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect:/myThings";
    }
    @PostMapping("/geraet/edit/{id}")
    public String geraetEdit(Model model, @PathVariable Long id,@ModelAttribute Geraet geraet, Principal person) {
        Geraet geraet1=geraetRepository.findById(id).get();
        geraet1.setKosten(geraet.getKosten());
        geraet1.setTitel(geraet.getTitel());
        geraet1.setBeschreibung(geraet.getBeschreibung());
        geraet1.setKaution(geraet.getKaution());
        geraet1.setAbholort(geraet.getAbholort());

        geraetRepository.save(geraet1);
        List<Geraet> geraete = null;//personRepository.findByUsername(person.getName()).get().getMyThings();
        model.addAttribute("geraete",geraete);
        return "myThings";
    }
    @GetMapping("/user/bezahlen/{id}")
    public String bezahlen(Model model,Principal person,@PathVariable Long id){
//        transferRepository.deleteAll();
        String name= person.getName();
        Person person1 =personRepository.findByUsername(name).get();
        Geraet geraet=geraetRepository.findById(id).get();

        Transfer newTransfer= new Transfer();
        newTransfer.setAbsender(person1.getUsername());
        newTransfer.setEmpfänger(geraet.getBesitzer());
        newTransfer.setAmount(geraet.getKosten());
        transferRepository.save(newTransfer);
        model.addAttribute(person1);
        return "confirmBezahlen";
    }

}
