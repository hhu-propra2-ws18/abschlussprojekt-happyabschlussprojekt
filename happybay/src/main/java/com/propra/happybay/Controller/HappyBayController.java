package com.propra.happybay.Controller;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.SecurityService;
import com.propra.happybay.Service.UserService;
import com.propra.happybay.Service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.management.modelmbean.ModelMBeanAttributeInfo;
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
    private UserService userService;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/")
    public String index(Model model){
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser(Model model) {
        model.addAttribute("person", new Person());
        return "addUser";
    }

    @PostMapping("/add")
    public String addToDatabase(@ModelAttribute("person")Person person, BindingResult bindingResult,Model model) {
        userValidator.validate(person, bindingResult);
        System.out.print(bindingResult);
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult",bindingResult);
            return "addUser";
        }
        userService.save(person);
        securityService.autologin(person.getUsername(), person.getPasswordConfirm());
        return "confirmationAdd";
    }

    @GetMapping("/admin")
    public String administrator() {
        return "admin";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout){
        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");
        return "login";
    }


    @GetMapping("/PersonInfo")
    public String userInfo(Model m, Principal person) {
        m.addAttribute("username", person.getName());
        return "PersonInfo";
    }

    @GetMapping("/PersonInfo/Profile")
    public String profile(Model model){
        return "profile";
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
