package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Model.HelperClassesForViews.PersonMitAccount;
import com.propra.happybay.Repository.*;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class NotificationControllerTest {
    private Person person = new Person();
    private Person person2 = new Person();
    private Account account = new Account();
    private Geraet geraet=new Geraet();
    private PersonMitAccount personMitAccount;

    private List<Geraet> geraetList=new ArrayList<>();
    private List<PersonMitAccount> personMitAccountList = new ArrayList<>();
    private Notification notification=new Notification();
    @Mock
    private PersonRepository personRepository;
    @Mock
    private AccountRepository accountRepository;

    private RentEvent rentEvent=new RentEvent();
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    RentEventService rentEventService;
    @Mock
    MailService mailService;
    @Mock
    RentEventRepository rentEventRepository;
    @Mock
    ProPayService proPayService;
    @Mock
    PersonService personService;
    @Mock
    NotificationService notificationService;
    @Mock
    NotificationRepository notificationRepository;
    @Autowired
    public PasswordEncoder encoder;
    private MockMvc mvc;
    Date start = new Date(2019,10,20);
    Date end = new Date(2019,11,21);
    private TimeInterval timeInterval = new TimeInterval(start,end);
    @Before
    public void setup() throws IOException {
        person.setUsername("person1");
        person.setId(1L);
        person.setAdresse("test dusseldorf");
        person2.setUsername("person2");
        person2.setId(2L);
        person2.setAdresse("test dusseldorf");
        personRepository.save(person);
        List<Reservation> reservationList = new ArrayList<>();
        account.setAccount(person.getUsername());
        account.setAmount(100.0);
        account.setReservations(reservationList);
        accountRepository.save(account);
        personMitAccount = new PersonMitAccount(person,account);

        //gerät
        geraet.setId(3L);
        geraet.setBesitzer(person);
        geraetList.add(geraet);
        personMitAccountList.add(personMitAccount);
        //
        rentEvent.setGeraet(geraet);
        rentEvent.setId(3L);
        rentEvent.setReservationId(2);
        rentEvent.setMieter(person);
        //
        notification.setId(3L);
        notification.setGeraet(geraet);
        notification.setAnfragePerson(person);
        notification.setMietezeitpunktStart(start);
        notification.setMietezeitpunktEnd(end);
        notification.setRentEvent(rentEvent);
        notification.setBesitzer(person2);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService,mailService,proPayService,personService,geraetRepository,notificationRepository,rentEventRepository,rentEventService))
                .setViewResolvers(viewResolver)
                .build();
    }
    @Test
    public void  notificationAcceptRequest() throws Exception {
        when(notificationRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(notification));
        mvc.perform(post("/user/notification/acceptRequest/{id}",3L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void notificationRefuseReturn() throws Exception {
        when(notificationRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(notification));

        mvc.perform(post("/user/notification/refuseReturn/{id}",3L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void notificationRefuseRequest() throws Exception {
        when(notificationRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(notification));

        mvc.perform(post("/user/notification/refuseRequest/{id}",3L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
        verify(mailService, Mockito.times(1)).sendRefuseRequestMail(person,geraet);
        verify(notificationRepository, Mockito.times(1)).deleteById(3L);
        verify(notificationRepository, Mockito.times(1)).findById(3L);


    }

    @Test
    public void notificationAcceptReturn() throws Exception {
        when(notificationService.getNotificationById(3L)).thenReturn(notification);
        doNothing().when(mailService).sendAcceptReturnMail(person,geraet);
        doNothing().when(personService).makeComment(geraet,person,"grund");
        doNothing().when(proPayService).ueberweisen("person1","person2",100);
        doNothing().when(proPayService).releaseReservation("person1",2);
        mvc.perform(post("/user/notification/acceptReturn/{id}",3L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
    }
}