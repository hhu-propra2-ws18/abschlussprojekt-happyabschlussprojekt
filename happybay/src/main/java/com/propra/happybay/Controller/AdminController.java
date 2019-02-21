package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.GeraetService;
import com.propra.happybay.Service.PersonService;
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
import java.util.List;

@Controller
@RequestMapping(value = {"/admin"})
public class AdminController {


    @Autowired
    private ProPayService proPayService;
    @Autowired
    private PersonService personService;
    @Autowired
    private AdminService adminService;


    @GetMapping(value = {"/", ""})
    public String adminFunktion(Model model){
        adminService.addModelCreate(model);
        return adminService.isInitPassword();
    }

    @GetMapping("/allUsers")
    public String allUsers(Model model) {
        adminService.addModelCreate(model);
        return "admin/allUsers";
    }

    @GetMapping("/conflicts")
    public String conflicts(Model model) {
        model.addAttribute("geraeteMitKonflikten", adminService.getGeraeteMitKonflikten());
        adminService.setAnzahlKonflikte();
        adminService.addModelCreate(model);
        return "admin/conflicts";
    }

    @GetMapping("/notifications")
    public String adminNotifications(Model model){
        adminService.setAnzahlNotifications();
        adminService.addModelCreate(model);
        model.addAttribute("transferRequests", adminService.getAllTransferRequest());
        return "admin/adminNotifications";
    }

    @PostMapping("/erhoeheAmount")
    public String erhoeheAmount(Model model, @ModelAttribute("username") String username) throws IOException {
        proPayService.erhoeheAmount(username, 10);
        proPayService.saveAccount(username);
        model.addAttribute("person", personService.getByUsername(username));
        model.addAttribute("account", adminService.getAccountByUsername(username));
        return "redirect:/admin/";
    }

    @PostMapping("/punishAccount")
    public String punishAccount(Model model, @ModelAttribute("username") String username, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        proPayService.punishReservation(username, adminService.getGeraeteMitReservationID(geraetId).getReservationID());
        adminService.setGeraetToNew(adminService.getGeraeteMitReservationID(geraetId).getGeraetID(), true);

        model.addAttribute("person", personService.getByUsername(username));
        model.addAttribute("account", adminService.getAccountByUsername(username));
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/releaseAccount")
    public String releaseAccount(Model model, @ModelAttribute("username") String username, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        GeraetMitReservationID geraetMitReservationID = adminService.getGeraeteMitReservationID(geraetId);
        proPayService.releaseReservation(username, geraetMitReservationID.getReservationID());
        adminService.setGeraetToNew(geraetId, false);
        model.addAttribute("person", personService.getByUsername(username));
        model.addAttribute("account", adminService.getAccountByUsername(username));
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/propay")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account) {
        adminService.saveTransfer(amount, account);
        return "redirect:/user/profile";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("newPassword") String newPassword) {
        adminService.setAdminNewPassword(newPassword);
        return "redirect:/admin";
    }
}
