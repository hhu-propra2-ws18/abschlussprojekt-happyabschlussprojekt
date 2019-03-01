package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private GeraetRepository geraetRepository;
    @Mock
    private PersonService personService;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void find_all_by_besitzer(){
        List<Notification> notificationList = new ArrayList<>();
        Notification fakeNotification1 = new Notification();
        Notification fakeNotification2 = new Notification();

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
        Person besitzer = new Person();

        fakeNotification1.setGeraet(fakeGeraet1);
        fakeNotification2.setGeraet(fakeGeraet2);

        notificationList.add(fakeNotification1);
        notificationList.add(fakeNotification2);
        Mockito.when(notificationRepository.findAllByBesitzer(besitzer)).thenReturn(notificationList);

        Assertions.assertThat(notificationService.findAllByBesitzer(besitzer).get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(notificationService.findAllByBesitzer(besitzer).get(1).getEncode()).isNotEqualTo(null);
    }


    //@Test
    //public void make_notification(){
    //    Person besitzer = new Person();
    //    Geraet fakeGeraet = new Geraet();
    //    fakeGeraet.setBesitzer(besitzer);
    //    Notification notification = new Notification();
    //    notification.setAnfragePerson(besitzer);
    //    notification.setGeraet(fakeGeraet);
    //    notification.setBesitzer(besitzer);
    //
    //    when(notificationRepository.save(any()).
    //    Assertions.assertThat(notificationService.copyAndEditNotification(besitzer,fakeGeraet, notification, "request"))
    //            .isEqualTo(notification);
    //}

    @Test
    public void updateAnzahl(){
        List<Notification> notificationList = new ArrayList<>();
        notificationList.add(new Notification());
        Person fakePerson = new Person();

        Mockito.when(notificationRepository.findAllByBesitzer(fakePerson)).thenReturn(notificationList);
        //Mockito.when(personService.getByUsername(anyString())).thenReturn(new Person());

        notificationService.updateAnzahlOfNotifications(fakePerson);
        verify(notificationRepository,times(1)).findAllByBesitzer(fakePerson);
        //verify(personService,times(1)).getByUsername("fakeUsername");

    }

    @Test
    public void getNotificationById(){
        Notification fake = new Notification();
        when(notificationRepository.findById(1L)).thenReturn(java.util.Optional.of(fake));
        Assertions.assertThat(notificationService.getNotificationById(1L)).isEqualTo(fake);
    }
}