package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = {"/admin"})
public class AdminController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private GeraetMitReservationIDRepository geraetMitReservationIDRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    public PasswordEncoder encoder;

    static int anzahlKonflikte = 0;
    static int anzahlPersonen = 0;
    static int anzahlNotifications = 0;

    @GetMapping(value = {"/", ""})
    public String adminFunktion(Model model){
        List<PersonMitAccount> personenMitAccounts = new ArrayList<>();
        List<Person> personList = personRepository.findAll();
        for (Person person : personList) {
            if (!person.getUsername().equals("admin")) {
                Optional<Account> account = accountRepository.findByAccount(person.getUsername());
                personenMitAccounts.add(new PersonMitAccount(person, account.get()));
            }
        }
        anzahlPersonen = personenMitAccounts.size();
        List<Geraet> geraeteMitKonflikten = geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
        anzahlKonflikte = geraeteMitKonflikten.size();
        model.addAttribute("personenMitAccounts", personenMitAccounts);
        model.addAttribute("anzahlPersonen", anzahlPersonen);
        model.addAttribute("anzahlKonflikte", anzahlKonflikte);
        model.addAttribute("anzahlNotifications", anzahlNotifications);
        Person admin = personRepository.findByUsername("admin").get();
        if (encoder.matches("admin", admin.getPassword())) {
            return "admin/changePassword";
        }
        return "admin/allUsers";
    }

    @GetMapping("/allUsers")
    public String allUsers(Model model) {
        List<PersonMitAccount> personenMitAccounts = new ArrayList<>();
        List<Person> personList = personRepository.findAll();
        for (Person person : personList) {
            if (!person.getUsername().equals("admin")) {
                Optional<Account> account = accountRepository.findByAccount(person.getUsername());
                personenMitAccounts.add(new PersonMitAccount(person, account.get()));
            }
        }
        anzahlPersonen = personenMitAccounts.size();
        model.addAttribute("personenMitAccounts",personenMitAccounts);
        model.addAttribute("anzahlPersonen", anzahlPersonen);
        model.addAttribute("anzahlKonflikte", anzahlKonflikte);
        model.addAttribute("anzahlNotifications", anzahlNotifications);
        return "admin/allUsers";
    }

    @GetMapping("/conflicts")
    public String conflicts(Model model) {
        List<Geraet> geraeteMitKonflikten = geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
        anzahlKonflikte = geraeteMitKonflikten.size();
        model.addAttribute("geraeteMitKonflikten", geraeteMitKonflikten);
        model.addAttribute("anzahlPersonen", anzahlPersonen);
        model.addAttribute("anzahlKonflikte", anzahlKonflikte);
        model.addAttribute("anzahlNotifications", anzahlNotifications);
        return "admin/conflicts";
    }

    @GetMapping("/notifications")
    public String adminNotifications(Model model){
        List<TransferRequest> transferRequests = transferRequestRepository.findAll();
        anzahlNotifications = transferRequests.size();
        model.addAttribute("transferRequests", transferRequests);
        model.addAttribute("anzahlPersonen", anzahlPersonen);
        model.addAttribute("anzahlKonflikte", anzahlKonflikte);
        model.addAttribute("anzahlNotifications", anzahlNotifications);
        return "admin/adminNotifications";
    }

    @PostMapping("/erhoeheAmount")
    public String erhoeheAmount(Model model, @ModelAttribute("username") String username) throws IOException {
        Person person = personRepository.findByUsername(username).get();
        model.addAttribute("person", person);
        proPayService.erhoeheAmount(person.getUsername(), 10);
        proPayService.saveAccount(person.getUsername());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "redirect:/admin/";
    }

    @PostMapping("/punishAccount")
    public String punishAccount(Model model, @ModelAttribute("username") String username, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        Person person = personRepository.findByUsername(username).get();
        model.addAttribute("person", person);
        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraetId);
        proPayService.punishReservation(username, geraetMitReservationID.getReservationID());
        Geraet geraet = geraetRepository.findById(geraetMitReservationID.getGeraetID()).get();
        geraet.setReturnStatus(ReturnStatus.DEFAULT);
        geraet.setVerfuegbar(true);
        geraet.setMieter(null);
        proPayService.saveAccount(geraet.getBesitzer());
        proPayService.saveAccount(geraet.getMieter());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/releaseAccount")
    public String releaseAccount(Model model, @ModelAttribute("username") String username, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        Person person = personRepository.findByUsername(username).get();
        model.addAttribute("person", person);
        GeraetMitReservationID geraetMitReservationID = geraetMitReservationIDRepository.findByGeraetID(geraetId);
        proPayService.releaseReservation(person.getUsername(), geraetMitReservationID.getReservationID());
        Geraet geraet = geraetRepository.findById(geraetMitReservationID.getGeraetID()).get();
        geraet.setVerfuegbar(true);
        geraet.setMieter(null);
        proPayService.saveAccount(geraet.getBesitzer());
        proPayService.saveAccount(geraet.getMieter());
        Account account = accountRepository.findByAccount(person.getUsername()).get();
        model.addAttribute("account", account);
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/propay")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account) {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(amount);
        transferRequest.setUsername(account);
        transferRequestRepository.save(transferRequest);
        return "redirect:/user/profile";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("newPassword") String newPassword) {
        Person admin = personRepository.findByUsername("admin").get();
        admin.setPassword(encoder.encode(newPassword));
        personRepository.save(admin);
        return "redirect:/admin";
    }
}
