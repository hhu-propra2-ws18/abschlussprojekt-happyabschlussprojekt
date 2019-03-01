package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void find_all_by_besitzer(){
        Geraet fakeGeraet1 = new Geraet();
        fakeGeraet1.setId(1L);
        List<Bild> bilder1 = new ArrayList<>();
        Bild fakeBild1 = new Bild();
        fakeBild1.setBild(new byte[0]);
        bilder1.add(fakeBild1);
        fakeGeraet1.setBilder(bilder1);

        Geraet fakeGeraet2 = new Geraet();
        fakeGeraet2.setId(2L);
        List<Bild> bilder2 = new ArrayList<>();
        Bild fakeBild2 = new Bild();
        fakeBild2.setBild("fake".getBytes());
        bilder2.add(fakeBild2);
        fakeGeraet2.setBilder(bilder2);

        List<Notification> notificationList = new ArrayList<>();
        Notification fakeNotification1 = new Notification();
        fakeNotification1.setGeraet(fakeGeraet1);
        Notification fakeNotification2 = new Notification();
        fakeNotification2.setGeraet(fakeGeraet2);
        notificationList.add(fakeNotification1);
        notificationList.add(fakeNotification2);


        Person besitzer = new Person();

        Mockito.when(notificationRepository.findAllByBesitzer(besitzer)).thenReturn(notificationList);
        List<Notification> testNotification = notificationService.findAllByBesitzer(besitzer);
        Assertions.assertThat(testNotification.get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(testNotification.get(1).getEncode()).isNotEqualTo(null);

    }


//    @Test
//    public void make_notification(){
//        Geraet fakeGeraet = new Geraet();
//        fakeGeraet.setBesitzer("fakeBesitzer");
//        fakeGeraet.setZeitraum(5);
//        Notification notification = new Notification();
//        notification.setAnfragePerson("fakeName");
//        notification.setGeraetId(1L);
//        notification.setBesitzer("fakeBesitzer");
//        notification.setZeitraum(5);
//        Assertions.assertThat(notificationService.copyAndEditNotification("fakeName",1L,fakeGeraet)).isEqualTo(notification);
//    }

    @Test
    public void update_anzahl_ofNotifications(){
        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(new Notification());
        notificationList.add(new Notification());
        Person fakePerson = mock(Person.class);

        Mockito.when(notificationRepository.findAllByBesitzer(fakePerson)).thenReturn(notificationList);

        notificationService.updateAnzahlOfNotifications(fakePerson);
        verify(notificationRepository,times(1)).findAllByBesitzer(fakePerson);
        verify(fakePerson,times(1)).setAnzahlNotifications(2);

    }

    @Test
    public void get_notification_by_id(){
        Notification fake = new Notification();
        when(notificationRepository.findById(1L)).thenReturn(java.util.Optional.of(fake));
        Assertions.assertThat(notificationService.getNotificationById(1L)).isEqualTo(fake);
    }

    @Test
    public void copy_and_edit_notification(){
        Notification fakeNotification = new Notification();
        fakeNotification.setMietezeitpunktStart(Date.valueOf("2020-01-01"));
        fakeNotification.setMietezeitpunktEnd(Date.valueOf("2020-02-03"));
        notificationService.copyAndEditNotification(new Person(),new Geraet(), fakeNotification,"typ");
        verify(notificationRepository,times(1)).save(any());
    }

    @Test
    public void make_new_notification(){
        notificationService.makeNewNotification(new Geraet(),new RentEvent(),"faketyp");
        verify(notificationRepository,times(1)).save(any());

    }
}