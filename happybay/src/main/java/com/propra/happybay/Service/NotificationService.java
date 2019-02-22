package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.UserServices.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private PersonService personService;

    public List<Notification> findAllByBesitzer(String besitzer) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(besitzer);
        for (Notification notification : notifications) {
            if (geraetRepository.
                    findById(notification.getGeraetId())
                    .get()
                    .getBilder()
                    .get(0)
                    .getBild()
                    .length > 0) {
                notification.setEncode(geraetRepository.findById(notification.getGeraetId()).get().getBilder().get(0).encodeBild());
            }
        }
        return notifications;
    }

    public Notification makeNotification(String signedInPersonUsername, Long id, Geraet geraet) {
        Notification notification = new Notification();
        notification.setAnfragePerson(signedInPersonUsername);
        notification.setGeraetId(id);
        notification.setBesitzer(geraet.getBesitzer());
        notification.setZeitraum(geraet.getZeitraum());
        return notification;
    }

    public void updateAnzahl(String name) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(name);
        Person person = personService.getByUsername(name);
        person.setAnzahlNotifications(notifications.size());
    }
}
