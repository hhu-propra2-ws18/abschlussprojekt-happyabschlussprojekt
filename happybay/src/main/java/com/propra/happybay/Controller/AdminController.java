package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.TransferRequestRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.AdminServices.AdminServiceImpl;
import com.propra.happybay.Service.GeraetService;
import com.propra.happybay.Service.ProPayService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AdminService adminService;
    @Autowired
    private AdminServiceImpl adminServiceImpl;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    @Autowired
    private GeraetService geraetService;

    @GetMapping(value = {"/", ""})
    public String adminFunktion(Model model){
        InformationForMenuBadges informationForMenuBadges = adminServiceImpl.returnInformationForMenuBadges();
        System.out.println("###############");
        System.out.println(informationForMenuBadges.getNumberOfConflicts());
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        if (adminServiceImpl.isAdminHasDefaultPassword()) {
            return "admin/changePassword";
        }
        return "redirect:/admin/allUsers";
    }

    @GetMapping("/allUsers")
    public String allUsers(Model model) {
        List<PersonMitAccount> personenMitAccounts = adminServiceImpl.returnAllPersonsWithAccounts();
        InformationForMenuBadges informationForMenuBadges = adminServiceImpl.returnInformationForMenuBadges();

        model.addAttribute("personenMitAccounts", personenMitAccounts);
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        return "admin/allUsers";
    }

    @GetMapping("/conflicts")
    public String conflicts(Model model) {
        List<Geraet> geraeteMitKonflikten = geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT);
        InformationForMenuBadges informationForMenuBadges = adminServiceImpl.returnInformationForMenuBadges();

        model.addAttribute("geraeteMitKonflikten", geraeteMitKonflikten);
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        return "admin/conflicts";
    }

    @GetMapping("/notifications")
    public String adminNotifications(Model model){
        List<TransferRequest> transferRequests = transferRequestRepository.findAll();
        InformationForMenuBadges informationForMenuBadges = adminServiceImpl.returnInformationForMenuBadges();

        model.addAttribute("transferRequests", transferRequests);
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        return "admin/adminNotifications";
    }

    @PostMapping("/erhoeheAmount")
    public String erhoeheAmount(Model model, @ModelAttribute("username") String username) throws IOException {
        proPayService.erhoeheAmount(username, 10);
        return "redirect:/admin/allUsers";
    }

    @PostMapping("/punishAccount")
    public String punishAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        proPayService.punishReservation(mieter, geraetId);
        geraetService.restoreToDefault(geraetId);
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/releaseAccount")
    public String releaseAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("geraetId") Long geraetId) throws IOException {
        proPayService.releaseReservation(mieter, geraetId);
        geraetService.restoreToDefault(geraetId);
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/propay")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account) {
        TransferRequest transferRequest = new TransferRequest(account, amount);
        transferRequestRepository.save(transferRequest);
        return "redirect:/user/profile";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("newPassword") String newPassword) {
        adminServiceImpl.changeAdminPassword(newPassword);
        return "redirect:/admin";
    }

    @PostMapping("/erhoehungAblehenen")
    public String erhoehungAblehnen() {
        // TODO
        return "redirect:/admin/notifications";
    }

    @PostMapping("/erhoehungGenehmigen")
    public String erhoehungGenehmigen() {
        // TODO
        return "redirect:/admin/notifications";
    }
}
