package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    //public Notification makeNotification(String signedInPersonUsername, Long id, Geraet geraet) {
    //    Notification notification = new Notification();
    //    notification.setAnfragePerson(signedInPersonUsername);
    //    notification.setGeraetId(id);
    //    notification.setBesitzer(geraet.getBesitzer());
    //    return notification;
    //}

    public void updateAnzahl(String name) {
        List<Notification> notifications = notificationRepository.findAllByBesitzer(name);
        Person person = personService.getByUsername(name);
        person.setAnzahlNotifications(notifications.size());
    }


//    private List<Notification> getAllNotification(){
//        List<Notification> notifications = new ArrayList<>();
//        notificationRepository.findAll().forEach(e -> notifications.add(e));
//        return notifications;
//    }
//
//    private boolean isConflictTime(Notification notification1, Notification notification2){
//        if(((notification1.getMietezeitpunktStart().getTime() <= notification2.getMietezeitpunktStart().getTime()
//                && notification1.getMietezeitpunktEnd().getTime() >= notification2.getMietezeitpunktStart().getTime())
//                ||(notification1.getMietezeitpunktEnd().getTime() >= notification2.getMietezeitpunktEnd().getTime()
//                && notification1.getMietezeitpunktStart().getTime() <= notification2.getMietezeitpunktEnd().getTime()))
//        || ((notification2.getMietezeitpunktStart().getTime() <= notification1.getMietezeitpunktStart().getTime()
//                && notification2.getMietezeitpunktEnd().getTime() >= notification1.getMietezeitpunktStart().getTime())
//                ||(notification2.getMietezeitpunktEnd().getTime() >= notification1.getMietezeitpunktEnd().getTime()
//                && notification2.getMietezeitpunktStart().getTime() <= notification1.getMietezeitpunktEnd().getTime()))){
//            return true;
//        }
//        return false;
//    }
//
//    private boolean hasConflict(List<Long> ids){
//        if(ids.size() > 1){
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isContained(List<Long> ids, Long Id){
//        for(Long id: ids){
//            if(id.longValue() == Id.longValue()){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void setConflictIds(){
//        List<Notification> notifications = getAllNotification();
//        for(){
//
//        }
//    }

}
