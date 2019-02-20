package com.propra.happybay.Controller;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping(value = {"/"})
public class DefaultController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    private UserValidator userValidator;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private ProPayService proPayService;

    @GetMapping("/")
    public String index(Model model, Principal principal, @RequestParam(value = "key", required = false, defaultValue = "") String key) {
        if(principal != null){
            String name = principal.getName();
            if(personRepository.findByUsername(name).isPresent()) {
                model.addAttribute("person", personRepository.findByUsername(name).get());

                List<Geraet> rentThings = geraetRepository.findAllByMieter(name);
                List<Geraet> remindRentThings = new ArrayList<>();
                List<Geraet> overTimeThings = new ArrayList<>();
                LocalDate deadLine = LocalDate.now().plusDays(3);
                for (Geraet geraet : rentThings) {
                    if (geraet.getEndzeitpunkt().isBefore(deadLine) || geraet.getEndzeitpunkt().isEqual(deadLine)) {
                        if (LocalDate.now().isAfter(geraet.getEndzeitpunkt())) {
                            overTimeThings.add(geraet);
                        } else {
                            remindRentThings.add(geraet);
                        }
                    }
                }
                model.addAttribute("remindRentThings", remindRentThings);
                model.addAttribute("overTimeThings", overTimeThings);
            }
        }
        List<Geraet> geraete = geraetRepository.findAllByTitelLike("%" + key + "%");
        System.out.println(key + geraete);
        //List<Geraet> geraete = geraetRepository.findAll();
        for (Geraet geraet: geraete){
            if (geraet.getBilder().size() == 0) {
                geraet.setBilder(null);
            }
            if (geraet.getBilder() != null && geraet.getBilder().size() > 0) {
                geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
            }

        }
        model.addAttribute("geraete", geraete);
        return "default/index";
    }

    @GetMapping("/register")
    public String register() {
        return "default/register";
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
            return "default/register";
        }
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setRole("ROLE_USER");
        person.setPassword(encoder.encode(person.getPassword()));

        personRepository.save(person);
        proPayService.saveAccount(person.getUsername());
        person.setEncode(encodeBild(person.getFoto()));
        person.setPassword("");
        model.addAttribute("person", person);
        return "default/confirmationOfRegistration";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Ihr Benutzername oder Kennwort sind nicht gültig.");

        if (logout != null)
            model.addAttribute("message", "Sie wurden erfolgreich abgemeldet.");
        model.addAttribute("person", new Person());
        return "default/login";
    }

    private String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }
}
