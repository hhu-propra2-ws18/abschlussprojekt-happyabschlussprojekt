package com.propra.happybay.Controller;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.Transfer;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.TransferRepository;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
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
            model.addAttribute("bindingResult", bindingResult);
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
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
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
    public String confirmGeraet(@ModelAttribute("geraet")Geraet geraet,Principal principal) {
        geraet.setVerfuegbar(true);
        geraet.setBesitzer(personRepository.findByUsername(principal.getName()).get());
        geraetRepository.save(geraet);
        return "index";
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
    @GetMapping("/user/bezahlen/{id}")
    public String bezahlen(Model model,Principal person,@PathVariable Long id){
//        transferRepository.deleteAll();
        String name= person.getName();
        Person person1 =personRepository.findByUsername(name).get();
        Geraet geraet=geraetRepository.findById(id).get();

        Transfer newTransfer= new Transfer();
        newTransfer.setAbsender(person1.getUsername());
        newTransfer.setEmpfänger(geraet.getBesitzer().getUsername());
        newTransfer.setAmount(geraet.getKosten());
        transferRepository.save(newTransfer);
        model.addAttribute(person1);
        return "confirmBezahlen";
    }
}
