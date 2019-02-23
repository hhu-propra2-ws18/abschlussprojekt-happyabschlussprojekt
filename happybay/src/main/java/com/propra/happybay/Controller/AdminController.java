package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Model.HelperClassesForViews.PersonMitAccount;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.GeraetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = {"/admin"})
public class AdminController {
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    private GeraetService geraetService;

    @GetMapping(value = {"/", ""})
    public String adminFunktion(Model model){
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        if (adminService.isAdminHasDefaultPassword()) {
            return "admin/changePassword";
        }
        return "redirect:/admin/allUsers";
    }

    @GetMapping("/allUsers")
    public String allUsers(Model model) {
        List<PersonMitAccount> personenMitAccounts = adminService.returnAllPersonsWithAccounts();
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();

        model.addAttribute("personenMitAccounts", personenMitAccounts);
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        return "admin/allUsers";
    }

    @GetMapping("/conflicts")
    public String conflicts(Model model) {
        List<GeraetWithRentEvent> geraetWithRentEventsWithConflicts = adminService.getGeraetWithRentEventsWithConflicts();
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
        model.addAttribute("geraetWithRentEventsWithConflicts", geraetWithRentEventsWithConflicts);
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        return "admin/conflicts";
    }

    @PostMapping("/punishAccount")
    public String punishAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("reservationId") int reservationId) throws IOException {
        RentEvent rentEvent = rentEventRepository.findByReservationId(reservationId);
        Geraet geraet = geraetRepository.findById(rentEvent.getGeraetId()).get();
        proPayService.punishReservation(mieter, geraet.getBesitzer(), reservationId, geraet.getKaution());
        geraetService.checkForTouchingIntervals(geraet, rentEvent);
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.delete(rentEvent);
        return "redirect:/admin/conflicts";
    }


    @PostMapping("/releaseAccount")
    public String releaseAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("reservationId") int reservationId) throws IOException {
        proPayService.releaseReservation(mieter, reservationId);
        RentEvent rentEvent = rentEventRepository.findByReservationId(reservationId);
        Geraet geraet = geraetRepository.findById(rentEvent.getGeraetId()).get();
        geraetService.checkForTouchingIntervals(geraet, rentEvent);
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.delete(rentEvent);
        return "redirect:/admin/conflicts";
    }

    @PostMapping("/propay")
    public String aufladenAntrag(@ModelAttribute("amount") int amount, @ModelAttribute("account") String account) throws IOException {
        proPayService.erhoeheAmount(account, amount);
        return "redirect:/";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("newPassword") String newPassword) {
        adminService.changeAdminPassword(newPassword);
        return "redirect:/admin";
    }

    // DAS IST OPTIONAL
    //@GetMapping("/notifications")
    //public String adminNotifications(Model model){
    //    List<TransferRequest> transferRequests = transferRequestRepository.findAll();
    //    InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
    //
    //    model.addAttribute("transferRequests", transferRequests);
    //    model.addAttribute("informationForMenuBadges", informationForMenuBadges);
    //    return "admin/adminNotifications";

    //}
    //@PostMapping("/erhoeheAmount")
    //public String erhoeheAmount(@ModelAttribute("username") String username) throws IOException {
    //    proPayService.erhoeheAmount(username, 10);
    //    return "redirect:/admin/allUsers";
    //}
}
