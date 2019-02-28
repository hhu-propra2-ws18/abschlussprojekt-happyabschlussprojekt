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
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class NotificationControllerTest {
    private Person person = new Person();
    private Account account = new Account();
    private Geraet geraet=new Geraet();
    private PersonMitAccount personMitAccount;

    private List<Geraet> geraetList=new ArrayList<>();
    private List<PersonMitAccount> personMitAccountList = new ArrayList<>();
    private InformationForMenuBadges informationForMenuBadges = new InformationForMenuBadges();
    @Mock
    private PersonRepository personRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    AdminService adminService;
    @Mock
    RentEvent rentEvent;
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
    @Before
    public void setup() throws IOException {
        person.setUsername("testAdmin");
        person.setId(1L);
        person.setAdresse("test dusseldorf");
        personRepository.save(person);
        informationForMenuBadges.setNumberOfConflicts(1);
        informationForMenuBadges.setNumberOfNotifications(1);
        informationForMenuBadges.setNumberOfPersons(1);
        List<Reservation> reservationList = new ArrayList<>();
        account.setAccount(person.getUsername());
        account.setAmount(100.0);
        account.setReservations(reservationList);
        accountRepository.save(account);
        personMitAccount = new PersonMitAccount(person,account);
        //
        rentEvent.setGeraet(geraet);
        rentEvent.setId(2L);
        rentEvent.setReservationId(2);
        List<RentEvent> rentEventList=new ArrayList<>();
        rentEventList.add(rentEvent);
        //gerät
        geraet.setId(2L);
        geraet.setRentEvents(rentEventList);
        geraetList.add(geraet);
        personMitAccountList.add(personMitAccount);
        when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService,mailService,proPayService,personService,geraetRepository,notificationRepository,rentEventRepository,rentEventService))
                .setViewResolvers(viewResolver)
                .build();
    }
    @Test
    public void notificatioxnAcceptReturn() {
    }

    @Test
    public void notificationRefuseReturn() {
    }

    @Test
    public void notificationAcceptRequest() {
    }

    @Test
    public void notificationRefuseRequest() {
    }
}