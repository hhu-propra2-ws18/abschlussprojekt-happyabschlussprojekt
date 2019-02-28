package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Model.HelperClassesForViews.PersonMitAccount;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.ProPayService;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.RentEventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class AdminControllerTest {
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

    RentEvent rentEvent=new RentEvent();
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    GeraetService geraetService;
    @Mock
    RentEventRepository rentEventRepository;
    @Mock
    ProPayService proPayService;
    @Mock
    RentEventService rentEventService;
    @Autowired
    public PasswordEncoder encoder;
    Date start = new Date(2019,10,20);
    Date end = new Date(2019,11,21);
    private TimeInterval timeInterval = new TimeInterval(start,end);
    private MockMvc mvc;
    @Before
    public void setup() throws IOException {
        person.setUsername("person1");
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

        //gerät
        geraet.setId(2L);
        geraet.setBesitzer(person);
        geraet.setKosten(100);
        geraetList.add(geraet);
        //
        rentEvent.setGeraet(geraet);
        rentEvent.setId(2L);
        rentEvent.setReservationId(2);
        rentEvent.setTimeInterval(timeInterval);
        List<RentEvent> rentEventList=new ArrayList<>();
        rentEventList.add(rentEvent);
        geraet.setRentEvents(rentEventList);
        when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(new AdminController(rentEventService,proPayService,adminService,geraetRepository,rentEventRepository,geraetService))
                .setViewResolvers(viewResolver)
                .build();
    }
    @Test
    public void adminFunktion() throws Exception {
        when(adminService.isAdminHasDefaultPassword()).thenReturn(false);
        mvc.perform(get("/admin/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void adminFunktionReturnChangePassword() throws Exception {
        when(adminService.isAdminHasDefaultPassword()).thenReturn(true);
        mvc.perform(get("/admin/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void allUsers() throws Exception {
        when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(get("/admin/allUsers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void one_conflicts_test() throws Exception {
        when(adminService.getGeraetWithRentEventsWithConflicts()).thenReturn(new ArrayList<>());
        when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        mvc.perform(get("/admin/conflicts"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void releaseAccount() throws Exception {
        when(rentEventRepository.findByReservationId(2)).thenReturn(rentEvent);
        when(geraetRepository.findById(any())).thenReturn(Optional.ofNullable(geraet));
        when(rentEventService.calculatePrice(any())).thenReturn(100.0);
        doNothing().when(proPayService).ueberweisen("user","person1", (int) 100.0);
        mvc.perform(post("/admin/releaseAccount").flashAttr("mieter","user").flashAttr("reservationId", 2))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void punishAccount() throws Exception {
        when(rentEventRepository.findByReservationId(2)).thenReturn(rentEvent);
        when(geraetRepository.findById(any())).thenReturn(Optional.ofNullable(geraet));
        mvc.perform(post("/admin/punishAccount").flashAttr("mieter","user").flashAttr("reservationId",2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void changePassword() throws Exception {
        mvc.perform(post("/admin/changePassword").flashAttr("newPassword","testAdmin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }




}