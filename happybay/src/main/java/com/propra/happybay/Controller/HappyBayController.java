package com.propra.happybay.Controller;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.ArrayList;
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
        System.out.println(persons.get(0).getRole());
        System.out.println(persons.get(0).getUsername());
        System.out.println(persons.get(0).getPassword());
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

    @GetMapping("/PersonInfo")
    public String personInfo(Model model){
        return "PersonInfo";
    }

    @GetMapping("/PersonInfo/Profile")
    public String profile(Model model){
        return "Profile";
    }

    @GetMapping("/PersonInfo/MyThings")
    public String myThings(Model model){
        return "myThings";
    }
    @GetMapping("/PersonInfo/RentThings")
    public String rentThings(Model model){
        return "rentThings";
    }
}
