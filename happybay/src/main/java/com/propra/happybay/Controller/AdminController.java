package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Model.HelperClassesForViews.PersonMitAccount;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.RentEventService;
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
    private GeraetRepository geraetRepository;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    private GeraetService geraetService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RentEventService rentEventService;

    @GetMapping(value = {"/", ""})
    public String adminFunktion(Model model){
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);

        if (adminService.isAdminHasDefaultPassword()) {
            return "admin/changePassword";
        }
        return "redirect:http://localhost:8080/admin/allUsers";
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
    public String punishAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("reservationId") int reservationId,
                                Model model) {
        RentEvent rentEvent = rentEventRepository.findByReservationId(reservationId);
        Geraet geraet = rentEvent.getGeraet();
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        try {
            proPayService.punishReservation(mieter, geraet.getBesitzer().getUsername(), reservationId, geraet.getKaution());
        } catch (IOException e) {
            return"/admin/propayAdminNotAvailable";
        }
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.delete(rentEvent);
        return "redirect://localhost:8080/admin/conflicts";
    }


    @PostMapping("/releaseAccount")
    public String releaseAccount(@ModelAttribute("mieter") String mieter, @ModelAttribute("reservationId") int reservationId,
                                 Model model) throws IOException {
        InformationForMenuBadges informationForMenuBadges = adminService.returnInformationForMenuBadges();
        model.addAttribute("informationForMenuBadges", informationForMenuBadges);
        try {
            proPayService.releaseReservation(mieter, reservationId);
        } catch (IOException e) {
            return"/admin/propayAdminNotAvailable";
        }
        RentEvent rentEvent = rentEventRepository.findByReservationId(reservationId);
        Geraet geraet = rentEvent.getGeraet();
        proPayService.ueberweisen(mieter, geraet.getBesitzer().getUsername(), (int) rentEventService.calculatePrice(rentEvent));
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        rentEventRepository.delete(rentEvent);
        return "redirect://localhost:8080/admin/conflicts";
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("newPassword") String newPassword) {
        adminService.changeAdminPassword(newPassword);
        return "redirect://localhost:8080/admin";
    }

    public AdminController(ProPayService proPayService, AdminService adminService, GeraetRepository geraetRepository, RentEventRepository rentEventRepository, GeraetService geraetService) {
        this.proPayService=proPayService;
        this.adminService=adminService;
        this.geraetRepository=geraetRepository;
        this.rentEventRepository=rentEventRepository;
        this.geraetService=geraetService;
    }
}
