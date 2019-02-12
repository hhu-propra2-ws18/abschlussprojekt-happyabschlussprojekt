package com.propra.happybay.Controller;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    //@Autowired
    //UpdatePersonsService updatePersonsService;

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
}
