package com.propra.happybay.Controller;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.NormalObject.BindingResultWithErrors;
import com.propra.happybay.NormalObject.GeraetMitZeit;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.*;
import com.propra.happybay.Service.DefaultServices.DefaultService;
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
    @Autowired
    private GeraetService geraetService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private PictureService pictureService;

    @GetMapping("/")
    public String index(Model model, Principal principal, @RequestParam(value = "key", required = false, defaultValue = "") String key) {
        if (principal != null) {
            String name = principal.getName();
            if (personRepository.findByUsername(name).isPresent()) {
                notificationService.updateAnzahl(name);
                List<Geraet> rentThings = geraetRepository.findAllByMieter(name);
                GeraetMitZeit geraetMitZeit=new GeraetMitZeit(rentThings);
                geraetMitZeit.pruefGeraetZeit();
                model.addAttribute("person", personRepository.findByUsername(name).get());
                model.addAttribute("remindRentThings", geraetMitZeit.getRemindRentThings());
                model.addAttribute("overTimeThings", geraetMitZeit.getOverTimeThings());
            }
            else {
                model.addAttribute("person", new Person());
            }
        }
        model.addAttribute("geraete", geraetService.getAllWithKeyWithBiler(key));
        return "default/index";
    }

    @GetMapping("/register")
    public String register() {
        return "default/register";
    }

    @PostMapping("/addNewUser")
    public String addToDatabase(@RequestParam(value = "file",required = false) MultipartFile file,
                                @ModelAttribute("person") Person person, BindingResult bindingResult,
                                Model model) throws IOException {
        userValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            BindingResultWithErrors bindingResultWithErrors=new BindingResultWithErrors(bindingResult);
            bindingResultWithErrors.findErrorList();
            model.addAttribute("errorList", bindingResultWithErrors.getErrorList());
            return "default/register";
        }
        person.setFoto(pictureService.getBildFromInput(file));
        person.setRole("ROLE_USER");
        person.setPassword(encoder.encode(person.getPassword()));

        personRepository.save(person);
        proPayService.saveAccount(person.getUsername());

        person.setPassword(null);
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


}
