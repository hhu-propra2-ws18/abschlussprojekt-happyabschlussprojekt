package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.MailService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import com.propra.happybay.Service.UserServices.RentEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(value = {"/user/notification"})
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ProPayService proPayService;
    @Autowired
    private PersonService personService;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private RentEventRepository rentEventRepository;
    @Autowired
    private RentEventService rentEventService;

    static String redirectAdress = System.getenv("REDIRECT_URL");

    private static String returnRedirectAdress() {
        if (redirectAdress == null) {
            return "localhost";
        }
        return redirectAdress;
    }


    public NotificationController(NotificationService notificationService, MailService mailService, ProPayService proPayService, PersonService personService, GeraetRepository geraetRepository, NotificationRepository notificationRepository, RentEventRepository rentEventRepository, RentEventService rentEventService) {
        this.notificationService = notificationService;
        this.mailService = mailService;
        this.proPayService = proPayService;
        this.personService = personService;
        this.geraetRepository = geraetRepository;
        this.notificationRepository = notificationRepository;
        this.rentEventRepository = rentEventRepository;
        this.rentEventService = rentEventService;
    }

    @PostMapping("/acceptReturn/{id}")
    public String notificationAcceptReturn(@PathVariable Long id, @ModelAttribute("grund") String grund,
                                           Model model) throws Exception {
        Notification notification = notificationService.getNotificationById(id);
        RentEvent rentEvent = notification.getRentEvent();
        Geraet geraet = rentEvent.getGeraet();
        Person mieter = rentEvent.getMieter();
        mailService.sendAcceptReturnMail(mieter, geraet);
        personService.makeComment(geraet, mieter, grund);
        double amount = rentEventService.calculatePrice(rentEvent);
        model.addAttribute("person", geraet.getBesitzer());
        try {
            proPayService.ueberweisen(notification.getAnfragePerson().getUsername(), notification.getBesitzer().getUsername(), (int) amount);
            proPayService.releaseReservation(mieter.getUsername(), rentEvent.getReservationId());
        } catch (IOException e) {
            return "user/propayNotAvailable";
        }
        geraet.getRentEvents().remove(rentEvent);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);
        rentEventRepository.delete(rentEvent);
        return "redirect:" + returnRedirectAdress() + "/user/notifications";
    }

    @PostMapping("/refuseReturn/{id}")
    public String notificationRefuseReturn(@PathVariable Long id, @ModelAttribute("grund") String grund) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        RentEvent rentEvent = notification.getRentEvent();
        rentEvent.setReturnStatus(ReturnStatus.KAPUTT);
        rentEvent.setGrundForReturn(grund);
        rentEventRepository.save(rentEvent);

        Person mieter = rentEvent.getMieter();
        Geraet geraet = rentEvent.getGeraet();
        mailService.sendRefuseReturnMail(mieter, geraet);
        personService.makeComment(geraet, mieter, grund);
        notificationRepository.deleteById(id);
        return "redirect:" + returnRedirectAdress() + "/user/notifications";
    }

    @PostMapping("/acceptRequest/{id}")
    public String notificationAcceptRequest(@PathVariable Long id, Model model) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        Person mieter = notification.getAnfragePerson();
        Geraet geraet = notification.getGeraet();
        model.addAttribute("person", geraet.getBesitzer());
        int reservationId = 0;
        try {
            reservationId = proPayService.erzeugeReservation(mieter.getUsername(), geraet.getBesitzer().getUsername(), geraet.getKaution());
        } catch (IOException e) {
            return "user/propayNotAvailable";
        }

        TimeInterval timeInterval = new TimeInterval(notification.getMietezeitpunktStart(), notification.getMietezeitpunktEnd());
        RentEvent rentEvent = new RentEvent();
        rentEvent.setMieter(mieter);
        rentEvent.setTimeInterval(timeInterval);
        rentEvent.setGeraet(geraet);
        rentEvent.setReservationId(reservationId);
        rentEvent.setReturnStatus(ReturnStatus.BOOKED);
        geraet.getRentEvents().add(rentEvent);
        System.out.println("#########");
        System.out.println(timeInterval.getStart());
        int index = personService.positionOfFreeBlock(geraet, rentEvent);
        personService.splitTimeIntervalsOfGeraetAvailability(geraet, index, rentEvent);
        geraetRepository.save(geraet);
        notificationRepository.deleteById(id);
        System.out.println("#########");
        System.out.println(rentEvent.getTimeInterval().getStart());

        Person person = mieter;
        mailService.sendAcceptRequestMail(person, geraet);
        return "redirect:" + returnRedirectAdress() + "/user/notifications";
    }

    @PostMapping("/refuseRequest/{id}")
    public String notificationRefuseRequest(@PathVariable Long id) throws Exception {
        Notification notification = notificationRepository.findById(id).get();
        Person mieter = notification.getAnfragePerson();
        Geraet geraet = notification.getGeraet();

        mailService.sendRefuseRequestMail(mieter, geraet);
        notificationRepository.deleteById(id);
        return "redirect:" + returnRedirectAdress() + "/user/notifications";
    }
}
