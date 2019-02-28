package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.MailService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = {"/user/geraet"})
public class GeraetController {
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PersonService personService;
    @Autowired
    private GeraetService geraetService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/edit/{id}")
    public String geraetEdit(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        Person person = geraet.getBesitzer();
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        return "user/edit";
    }

    @GetMapping("/zurueckgeben/{id}")
    public String geraetZurueck(@PathVariable Long id) throws Exception {
        RentEvent rentEvent = rentEventRepository.findById(id).get();
        rentEvent.setReturnStatus(ReturnStatus.WAITING_FOR_CONFIRMATION);
        rentEventRepository.save(rentEvent);
        Geraet geraet = rentEvent.getGeraet();
        notificationService.makeNewNotification(geraet, rentEvent, "return");
        Person besitzer = geraet.getBesitzer();
        mailService.sendReturnMail(besitzer, geraet);
        return "redirect://localhost:8080/user/rentThings";
    }

    @PostMapping("/delete/{id}")
    public String geraetDelete(@PathVariable Long id) {
        geraetRepository.deleteById(id);
        return "redirect://localhost:8080/user/myThings";
    }


    @GetMapping("/addLikes/{id}")
    public String like(@PathVariable Long id, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        geraetService.addLike(id, person);
        return "redirect://localhost:8080";
    }

    @PostMapping("/edit/{id}")
    public String geraetEdit(Model model, @PathVariable Long id, @ModelAttribute Geraet geraet,
                             @RequestParam(value = "files",required = false) MultipartFile[] files) throws IOException {
        geraetService.editGeraet(files, geraet, id, false);
        List<Geraet> geraete = null;
        model.addAttribute("geraete", geraete);
        return "redirect://localhost:8080/user/myThings";
    }

    @GetMapping("/{id}")
    public String geraet(@PathVariable Long id, Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        Geraet geraet = geraetRepository.findById(id).get();
        Person personForComment = geraet.getBesitzer();
        List<String> encodes = geraetService.geraetBilder(geraet);
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account",account);
        model.addAttribute("encodes", encodes);
        model.addAttribute("person", person);
        model.addAttribute("geraet", geraet);
        model.addAttribute("personForComment", personForComment);
        return "user/geraet";
    }

    @GetMapping("/changeToRent/{id}")
    public String changeToRent(@PathVariable Long id, Model model) {
        Geraet geraet = geraetRepository.findById(id).get();
        model.addAttribute("person", geraet.getBesitzer());
        model.addAttribute("geraet", geraet);
        return "user/changeToRent";
    }

    @PostMapping("/changeToRent/{id}")
    public String changeToRent(@PathVariable Long id, @ModelAttribute("geraet") Geraet geraet, @RequestParam(value = "files",required = false) MultipartFile[] files) throws IOException {
        RentEvent verfuegbar = new RentEvent();
        TimeInterval timeIntervalWithout = new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd());
        TimeInterval timeInterval = geraetService.convertToCET(timeIntervalWithout);
        verfuegbar.setTimeInterval(timeInterval);

        Geraet geraet1 = geraetRepository.findById(id).get();
        geraetService.editGeraet(files, geraet, id, false);
        geraet1.getVerfuegbareEvents().add(verfuegbar);
        geraetRepository.save(geraet1);
        return "redirect://localhost:8080/user/myThings";
    }

    @GetMapping("/addGeraet")
    public String addGeraet(Model model, Principal principal) {
        Person person = personService.findByPrincipal(principal);
        model.addAttribute("person", person);
        return "user/addGeraet";
    }

    @PostMapping("/addGeraet")
    public String confirmGeraet(@ModelAttribute("geraet") Geraet geraet, @RequestParam(name = "files",value = "files",
            required = false) MultipartFile[] files, Principal principal) throws IOException {
        List<Bild> bilds = new ArrayList<>();
        personService.umwechsleMutifileZumBild(files, bilds);
        RentEvent verfuegbar = new RentEvent();
        TimeInterval timeIntervalWithout = new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd());
        TimeInterval timeInterval = geraetService.convertToCET(timeIntervalWithout);
        if (!geraet.isForsale()) {
            verfuegbar.setTimeInterval(timeInterval);
            geraet.getVerfuegbareEvents().add(verfuegbar);
        }
        geraet.setBilder(bilds);
        geraet.setLikes(0);
        Person person = personRepository.findByUsername(principal.getName()).get();
        geraet.setBesitzer(person);
        geraetRepository.save(geraet);
        return "redirect://localhost:8080/user/myThings";
    }
}
