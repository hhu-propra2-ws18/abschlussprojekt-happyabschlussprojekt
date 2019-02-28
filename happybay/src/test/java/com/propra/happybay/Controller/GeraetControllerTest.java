package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.MailService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class GeraetControllerTest {
    private Person person1 = new Person();
    private Account account1 = new Account();
    private Geraet geraet1 =new Geraet();
    private RentEvent rentEvent=new RentEvent();
    private List<Geraet> geraetList=new ArrayList<>();
    Date start = new Date(2019,10,20);
    Date end = new Date(2019,11,21);
    private MockMvc mvc;
    Principal principal = () -> "user";
    MultipartFile[] multipartFiles = new MultipartFile[1];

    @Mock
    private PersonRepository personRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    GeraetService geraetService;
    @Mock
    MailService mailService;
    @Mock
    RentEventRepository rentEventRepository;
    @Mock
    PersonService personService;
    @Mock
    NotificationService notificationService;

    @Before
    public void setup() {
        //person
        person1.setUsername("person1");
        person1.setId(1L);
        person1.setAnzahlNotifications(0);
        person1.setAdresse("hanoi");
        List<Reservation> reservationList = new ArrayList<>();
        account1.setAccount(person1.getUsername());
        account1.setAmount(100.0);
        account1.setReservations(reservationList);
        List<RentEvent> rentEventList=new ArrayList<>();
        rentEventList.add(rentEvent);
        //gerät1 von person1 bzw..
        geraet1.setId(3L);
        geraet1.setRentEvents(rentEventList);
        geraet1.setBesitzer(person1);
        geraet1.setMietezeitpunktStart(start);
        geraet1.setMietezeitpunktEnd(end);
        geraetList.add(geraet1);
        //rentEvent
        rentEvent.setGeraet(geraet1);
        rentEvent.setId(3L);
        rentEvent.setReservationId(2);
        //viewResolver
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        //build MVC
        mvc = MockMvcBuilders.standaloneSetup(new GeraetController(geraetRepository,rentEventRepository,notificationService,mailService,personService,geraetService,accountRepository,personRepository))
                .setViewResolvers(viewResolver)
                .build();
    }
    @Test
    public void geraetZurueck() throws Exception {
        when(rentEventRepository.findById(1L)).thenReturn(java.util.Optional.of(rentEvent));
        doNothing().when(notificationService).makeNewNotification(geraet1,rentEvent,"return");
        mvc.perform(get("/user/geraet/zurueckgeben/{id}",1L).principal(principal))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080/user/rentThings"));
        verify(mailService, Mockito.times(1)).sendReturnMail(person1, geraet1);
        verify(notificationService, Mockito.times(1)).makeNewNotification(geraet1,rentEvent,"return");
        verify(rentEventRepository, Mockito.times(1)).save(rentEvent);
        verify(rentEventRepository, Mockito.times(1)).findById(1L);

    }
    @Test
    public void geraetDelete() throws Exception {
        mvc.perform(post("/user/geraet/delete/{id}",1L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080/user/myThings"));
        verify(geraetRepository, Mockito.times(1)).deleteById(any());

    }

    @Test
    public void like() throws Exception {
        when(personService.findByPrincipal(principal)).thenReturn(person1);
        doNothing().when(geraetService).addLike(any(),any());

        mvc.perform(get("/user/geraet/addLikes/{id}",1L).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080"));
        verify(geraetService, Mockito.times(1)).addLike(1L,person1);

    }
    @Test
    public void geraetEdit() throws Exception {

        mvc.perform(post("/user/geraet/edit/{id}",1L).contentType(MediaType.APPLICATION_JSON).flashAttr("person1", person1).flashAttr("geraet",geraet1).requestAttr("file",multipartFiles).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080/user/myThings"));
        verify(geraetService, Mockito.times(1)).editGeraet(null,geraet1,1L,false);

    }
    @Test
    public void geraetEditGET() throws Exception {
        when(geraetRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(geraet1));
        mvc.perform(get("/user/geraet/edit/{id}",1L))
                .andExpect(status().isOk())
                .andExpect(view().name("user/edit"));
        verify(geraetRepository, Mockito.times(1)).findById(1L);

    }

    @Test
    public void geraet() throws Exception {
        when(personService.findByPrincipal(principal)).thenReturn(person1);
        when(geraetRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(geraet1));
        when(accountRepository.findByAccount(anyString())).thenReturn(java.util.Optional.ofNullable(account1));
        mvc.perform(get("/user/geraet/{id}",3L).principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("user/geraet"));
        verify(personService, Mockito.times(1)).findByPrincipal(principal);
        verify(geraetRepository, Mockito.times(1)).findById(3L);
        verify(geraetService, Mockito.times(1)).geraetBilder(geraet1);
        verify(accountRepository, Mockito.times(1)).findByAccount("person1");

    }

    @Test
    public void changeToRentGET() throws Exception {
        when(geraetRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(geraet1));

        mvc.perform(get("/user/geraet/changeToRent/{id}",3L))
                .andExpect(status().isOk())
                .andExpect(view().name("user/changeToRent"));
        verify(geraetRepository, Mockito.times(1)).findById(3L);

    }
    @Test
    public void changeToRentPOST() throws Exception {
        when(geraetRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(geraet1));
        mvc.perform(post("/user/geraet/changeToRent/{id}",3L).contentType(MediaType.APPLICATION_JSON).flashAttr("geraet", geraet1).requestAttr("files",multipartFiles))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080/user/myThings"));
        verify(geraetService, Mockito.times(1)).convertToCET(any());
        verify(geraetRepository, Mockito.times(1)).findById(3L);
        verify(geraetService, Mockito.times(1)).editGeraet(null,geraet1,3L,false);
        verify(geraetRepository, Mockito.times(1)).save(geraet1);

    }

    @Test
    public void addGeraetPost() throws Exception {

        when(personRepository.findByUsername("user")).thenReturn(java.util.Optional.ofNullable(person1));
        mvc.perform(post("/user/geraet/addGeraet").flashAttr("geraet", geraet1).requestAttr("files",multipartFiles).principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect://localhost:8080/user/myThings"));
        verify(personService, Mockito.times(1)).umwechsleMutifileZumBild(any(),any());
        verify(geraetService, Mockito.times(1)).convertToCET(any());
        verify(personRepository, Mockito.times(1)).findByUsername("user");
        verify(geraetRepository, Mockito.times(1)).save(geraet1);

    }
    @Test
    public void addGeraetGet() throws Exception {
        mvc.perform(get("/user/geraet/addGeraet").principal(principal))
                .andExpect(status().isOk())
                .andExpect(view().name("user/addGeraet"));
        verify(personService, Mockito.times(1)).findByPrincipal(principal);

    }
}