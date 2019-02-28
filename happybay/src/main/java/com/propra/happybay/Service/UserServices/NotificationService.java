package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PersonRepository personRepository;

    public List<Notification> findAllByBesitzer(Person besitzer) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(besitzer);
        for (Notification notification : notifications) {
            //if (notification.getGeraet()
            //        .getBilder()
            //        .get(0)
            //        .getBild()
            //        .length > 0) {
            //    notification.setEncode(notification.getGeraet().getBilder().get(0).encodeBild());
            //}
        }
        return notifications;
    }

    public void updateAnzahlOfNotifications(Person person) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(person);
        person.setAnzahlNotifications(notifications.size());
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).get();
    }

    public void copyAndEditNotification(Person anfragePerson, Geraet geraet, Notification notification, String typ) {
        Date startDate = new Date(notification.getMietezeitpunktStart().getTime() + 60 * 60 * 6000);
        Date endDate = new Date(notification.getMietezeitpunktEnd().getTime() + 60 * 60 * 6000);
        notification.setType(typ);
        notification.setAnfragePerson(anfragePerson);
        notification.setGeraet(geraet);
        notification.setMietezeitpunktStart(startDate);
        notification.setMietezeitpunktEnd(endDate);
        notification.setBesitzer(geraet.getBesitzer());
        notification.setEncode(geraet.getEncode());
        notificationRepository.save(notification);
    }

    public void makeNewNotification(Geraet geraet, RentEvent rentEvent, String typ) {
        Notification newNotification = new Notification();
        newNotification.setType(typ);
        newNotification.setAnfragePerson(rentEvent.getMieter());
        newNotification.setGeraet(rentEvent.getGeraet());
        newNotification.setRentEvent(rentEvent);
        newNotification.setBesitzer(geraet.getBesitzer());
        notificationRepository.save(newNotification);
    }
}
