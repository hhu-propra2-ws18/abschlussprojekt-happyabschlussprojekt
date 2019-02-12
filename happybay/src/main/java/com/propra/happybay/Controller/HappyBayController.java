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

    @GetMapping("/")
    public String index(Model model,Principal person){
        if (person != null) {
            Person person1 = personRepository.findByUsername(person.getName()).get();
            model.addAttribute("person",person1);
        }
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/add")
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
        return "profile";
    }


    @GetMapping("/PersonInfo/{id}")
    public String user(Model model,@PathVariable Long id) {
        if (id != null) {
            Person person = personRepository.findById(id).get();
            model.addAttribute("user", person);
        }
        return "personInfo";
    }

    @GetMapping("/PersonInfo/Profile/{id}")
    public String profile(Model model,@PathVariable Long id){
        Person person = personRepository.findById(id).get();
        model.addAttribute("user", person);
        return "profile";
    }

    @GetMapping("/PersonInfo/MyThings/{id}")
    public String myThings(Model model,@PathVariable Long id){
        Person person = personRepository.findById(id).get();
        model.addAttribute("user", person);
        return "myThings";
    }

    @GetMapping("/PersonInfo/RentThings/{id}")
    public String rentThings(Model model,@PathVariable Long id){
        Person person = personRepository.findById(id).get();
        model.addAttribute("user", person);
        return "rentThings";
    }
}
