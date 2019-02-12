package com.propra.happybay.Controller;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.sql.Date;
import java.util.List;

@Controller
public class HappyBayController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    public PasswordEncoder encoder;



    @GetMapping("/")
    public String index(Model model){
        List<Person> persons = personRepository.findAll();
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @GetMapping("/add")
    public String addToDatabase(@ModelAttribute("person")Person person,
                                Model model) {
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
        return "user";
    }

    @GetMapping("/geraet")
    public String test(Model model){
        Geraet geraet = new Geraet();
        geraet.setVerfuegbar(true);
        geraet.setTitel("iPhone Xs MAX");
        geraet.setOeffdatum(Date.valueOf("2019-1-15"));
        geraet.setKosten(20);
        geraet.setKaution(1400);
        geraet.setBesitzer(new Person());
        geraet.setBeschreibung("6,5\" Super Retina Display – das bislang größte iPhone Display. Weiterentwickeltes Face ID. Der intelligenteste, leistungsstärkste Smartphone Chip. Und ein revolutionäres Dual-Kamerasystem. Das iPhone XS Max ist alles, was du am iPhone liebst, und mehr.");
        geraet.setAbholort("Dusseldorf");
        model.addAttribute("geraet",geraet);
        return "geraet";
    }

}
