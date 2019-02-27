package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (notification.getGeraet()
                    .getBilder()
                    .get(0)
                    .getBild()
                    .length > 0) {
                notification.setEncode(notification.getGeraet().getBilder().get(0).encodeBild());
            }
        }
        return notifications;
    }

    //public Notification makeNotification(String signedInPersonUsername, Long id, Geraet geraet) {
    //    Notification notification = new Notification();
    //    notification.setAnfragePerson(signedInPersonUsername);
    //    notification.setGeraetId(id);
    //    notification.setBesitzer(geraet.getBesitzer());
    //    return notification;
    //}

    public void updateAnzahlOfNotifications(Person person) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(person);
        person.setAnzahlNotifications(notifications.size());
        personRepository.save(person);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).get();
    }
}
