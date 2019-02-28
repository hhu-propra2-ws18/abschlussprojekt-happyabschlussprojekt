package com.propra.happybay.Controller;

import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.DefaultServices.UserValidator;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = {"/"})
@ControllerAdvice
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
    private GeraetService geraetService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    private PersonService personService;

    public DefaultController(UserValidator userValidator, RentEventRepository rentEventRepository, GeraetService geraetService, PersonService personService, PersonRepository personRepository, GeraetRepository geraetRepository, NotificationService notificationService) {
        this.personRepository=personRepository;
        this.geraetRepository=geraetRepository;
        this.personService=personService;
        this.notificationService=notificationService;
        this.geraetService=geraetService;
        this.rentEventRepository=rentEventRepository;
        this.userValidator=userValidator;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal, @RequestParam(value = "key", required = false, defaultValue = "") String key) {
        model.addAttribute("geraete", geraetService.getAllWithKeyWithBilder(key));

        if (principal == null) {
            return "default/index";
        }

        String name = principal.getName();
        notificationService.updateAnzahl(name);
        model.addAttribute("person", personRepository.findByUsername(name).get());

        List<RentEvent> rentEventsDedlineisClose = rentEventRepository.findAllByMieterAndReturnStatus(name, ReturnStatus.DEADLINE_CLOSE);
        List<GeraetWithRentEvent> remindRentThings = geraetService.returnGeraeteWithRentEvents(rentEventsDedlineisClose);
        model.addAttribute("remindRentThings", remindRentThings);

        List<RentEvent> rentEventsDeadlineOver = rentEventRepository.findAllByMieterAndReturnStatus(name, ReturnStatus.DEADLINE_OVER);
        List<GeraetWithRentEvent> overTimeThings = geraetService.returnGeraeteWithRentEvents(rentEventsDeadlineOver);
        model.addAttribute("overTimeThings", overTimeThings);

        return "default/index";
    }

    @GetMapping("/register")
    public String register() {
        return "default/register";
    }

    @PostMapping("/addNewUser")
    public String addToDatabase(@RequestParam(value = "file",name= "file",required = false) MultipartFile file,
                                @ModelAttribute("person") Person person, BindingResult bindingResult,
                                Model model)  {
        userValidator.validate(person, bindingResult);
        model.addAttribute("person", person);
        if (bindingResult.hasErrors()) {
            List<String> errorList = new ArrayList<>();
            for (int i=0; i< bindingResult.getAllErrors().size(); i++){
                errorList.add(bindingResult.getAllErrors().get(i).getCode());
            }
            model.addAttribute("errorList", errorList);
            return "default/register";
        }
        try {
            personService.makeAndSaveNewPerson(file, person);
        } catch (Exception e) {
            return "/user/propayNotAvailable";
        }
        person.setPassword(""); // to not send real password to view
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

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    String permittedSizeException (Exception e){
        e.printStackTrace();
        return "<h3>The file exceeds its maximum permitted size of 15 MB. Please reload your page.</h3>" +
                "    <div>\n" +
                "            <span>\n" +
                "                <a href=\"/\">Or if you want to back to home</a>\n" +
                "            </span>\n" +
                "    </div>";
    }
}
