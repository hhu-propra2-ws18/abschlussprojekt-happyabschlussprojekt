package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.*;
import com.propra.happybay.Service.ProPayService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class UserControllerTest {
    private Person user = new Person();
    private Person admin = new Person();
    private Geraet geraet = new Geraet();
    private Account account = new Account();
    private RentEvent rentEvent=new RentEvent();
    private List<RentEvent> verfuegbareEvents=new ArrayList<>();
    Date start = new Date(2019,10,20);
    Date end = new Date(2019,11,21);
    private TimeInterval timeInterval = new TimeInterval(start,end);
    //Bild
    private Bild bild = new Bild();
    private RentEvent verfuerbar = new RentEvent();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    public PasswordEncoder encoder;
    private MockMvc mvc2;
    @Mock
    ProPayService proPayService;
    @Mock
    PersonRepository personRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    NotificationService notificationService;
    private Notification notification=new Notification();
    @Mock
    MailService mailService;
    @Mock
    PersonService personService;
    @Mock
    GeraetService geraetService;
    @Mock
    NotificationRepository notificationRepository;
    @Mock
    RentEventRepository rentEventRepository;
    Principal principal = new Principal() {
        @Override
        public String getName() {
            return "test";
        }
    };
    final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.png");
    final MockMultipartFile mockMultipartFile = new MockMultipartFile("test.png", "test.png", "image/png", inputStream);

    MultipartFile[] multipartFiles = new MultipartFile[1];

    public UserControllerTest() throws IOException {
    }

    @Before
    public void setup() throws IOException {
        multipartFiles[0] = mockMultipartFile;
        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);
        bild.setBild(bytes);
        //User
        user.setUsername("test");
        user.setId(1L);
        user.setFoto(bild);
        user.setRole("ROLE_USER");
        user.setAdresse("test dusseldorf");
        user.setAnzahlNotifications(0);

        //Admin
        admin.setUsername("admin");
        admin.setId(2L);
        admin.setFoto(bild);
        admin.setRole("ROLE_ADMIN");

        personRepository.save(admin);
        //VerfügbarEvent
        rentEvent.setGeraetId(2L);
        rentEvent.setMieter(user.getUsername());
        rentEvent.setTimeInterval(timeInterval);
                verfuegbareEvents.add(rentEvent);
        //Geraet
        geraet.setTitel("Das ist ein Test");
        geraet.setId(2L);
        geraet.setBesitzer(user.getUsername());
        geraet.setKosten(3);
        geraet.setKaution(10);
        geraet.setMietezeitpunktEnd(end);
        geraet.setMietezeitpunktStart(start);
        geraet.setVerfuegbareEvents(verfuegbareEvents);
       // geraetRepository.save(geraet);

        //Account
        account.setAccount(user.getUsername());
        account.setAmount(100.0);
        //
        notification.setGeraetId(3L);
        notification.setMietezeitpunktStart(start);
        notification.setMietezeitpunktEnd(end);
        notification.setRentEventId(1L);
        verfuerbar.setTimeInterval(geraetService.convertToCET(timeInterval));
        geraet.getVerfuegbareEvents().add(verfuerbar);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        //
        doNothing().when(notificationService).updateAnzahlOfNotifications(anyString());
        //notificationRepository
        doReturn(notificationRepository.save(new Notification())).when(notificationRepository).save(any());
        when(notificationRepository.findById(any())).thenReturn(Optional.ofNullable(notification));
        //
        when(personRepository.findByUsername(any())).thenReturn(Optional.ofNullable(user));
        when(geraetService.convertToCET(any())).thenReturn(timeInterval);
        when(personRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        doNothing().when(personService).checksActiveOrInActiveRentEvent(any(),any());
        when(rentEventRepository.findAllByMieterAndReturnStatus(any(),any())).thenReturn(verfuegbareEvents);
        when(geraetRepository.findById(any())).thenReturn(Optional.ofNullable(geraet));
        when(rentEventRepository.findById(any())).thenReturn(Optional.ofNullable(rentEvent));
        when(notificationService.getNotificationById(any())).thenReturn(notification);
        mvc2 = MockMvcBuilders.standaloneSetup(new UserController(proPayService,accountRepository,geraetService,mailService,notificationRepository,personService,rentEventRepository, personRepository,geraetRepository,notificationService))
                .setViewResolvers(viewResolver)
                .build();



    }

    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void proflieWithUser() throws Exception {
        mvc2.perform(get("/user/profile").principal(principal))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void myThings() throws Exception {
        mvc2.perform(get("/user/myThings").principal(principal))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void rentThings() throws Exception {


        mvc2.perform(get("/user/rentThings").principal(principal))
                .andExpect(status().isOk());
        verify(rentEventRepository, Mockito.times(6)).findAllByMieterAndReturnStatus(any(),any());

    }

    @Test
    public void makeNotifications() throws Exception {
        mvc2.perform(get("/user/notifications").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void anfragenGet() throws Exception {

       when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
       when(accountRepository.findByAccount(user.getUsername())).thenReturn(Optional.ofNullable(account));

       mvc2.perform(get("/user/anfragen/{id}",2L).principal(principal))
                .andExpect(status().isOk());
    }
    @Test
    public void anfragenPost() throws Exception {

        when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
        doNothing().when(mailService).sendAnfragMail(any(),any(),any());
        mvc2.perform(post("/user/anfragen/{id}",2L).principal(principal)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("notification",notification))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void addGeraetGet() throws Exception {
        mvc2.perform(get("/user/addGeraet").principal(principal))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void addGeraetPost() throws Exception {


        mvc2.perform(post("/user/addGeraet").flashAttr("geraet", geraet).requestAttr("files",multipartFiles).principal(principal))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void proPay() throws Exception {

        mvc2.perform(get("/user/proPay").principal(principal))
                .andExpect(status().isOk());

    }

    @Test
    public void besitzerInfo() throws Exception {
        mvc2.perform(get("/user/BesitzerInfo/{id}",1L).principal(principal))
                .andExpect(status().isOk());
    }
    @Test
    public void geraetZurueck() throws Exception {


        mvc2.perform(get("/user/geraet/zurueckgeben/{id}",1L).principal(principal))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void changeToRentPOST() throws Exception {

        mvc2.perform(post("/user/geraet/changeToRent/{id}",1L).contentType(MediaType.APPLICATION_JSON).flashAttr("geraet",geraet).requestAttr("files",multipartFiles))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void changeToRentGET() throws Exception {

        mvc2.perform(get("/user/geraet/changeToRent/{id}",1L))
                .andExpect(status().isOk());
    }
    @Test
    public void geraetEditGET() throws Exception {

        mvc2.perform(get("/user/geraet/edit/{id}",1L))
                .andExpect(status().isOk());
    }
    @Test
    public void geraet() throws Exception {

        mvc2.perform(get("/user/geraet/{id}",1L).principal(principal))
                .andExpect(status().isOk());
    }
    @Test
    public void aufladenAntrag() throws Exception {

        mvc2.perform(post("/user/propayErhoehung").param("amount","100").param("account","test"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void anfragen() throws Exception {

        mvc2.perform(post("/user/sale/{id}",1L).principal(principal))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void  notificationAcceptRequest() throws Exception {

        mvc2.perform(post("/user/notification/acceptRequest/{id}",1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void notificationRefuseReturn() throws Exception {

        mvc2.perform(post("/user/notification/refuseReturn/{id}",1L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void notificationRefuseRequest() throws Exception {

        mvc2.perform(post("/user/notification/refuseRequest/{id}",1L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
        verify(geraetRepository, Mockito.times(1)).findById(any());

    }
    @Test
    public void geraetDelete() throws Exception {

        mvc2.perform(post("/user/geraet/delete/{id}",1L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
        verify(geraetRepository, Mockito.times(1)).deleteById(any());

    }
    @Test
    public void notificationAcceptReturn() throws Exception {

        mvc2.perform(post("/user/notification/acceptReturn/{id}",1L).contentType(MediaType.APPLICATION_JSON).param("grund","grund"))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void changeImg() throws Exception {

        mvc2.perform(get("/user/PersonInfo/Profile/ChangeProfile").principal(principal))
                .andExpect(status().isOk());
    }
    @Test
    public void chageProfile() throws Exception {

        mvc2.perform(post("/user/PersonInfo/Profile/ChangeProfile").contentType(MediaType.APPLICATION_JSON).flashAttr("person",user).requestAttr("file",multipartFiles).principal(principal))
                .andExpect(status().isOk());

    }
    @Test
    public void geraetEdit() throws Exception {

        mvc2.perform(post("/user/geraet/edit/{id}",1L).contentType(MediaType.APPLICATION_JSON).flashAttr("person",user).requestAttr("file",multipartFiles).principal(principal))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @Test
    public void like() throws Exception {

        mvc2.perform(get("/user/geraet/addLikes/{id}",1L).principal(principal))
                .andDo(print())
                .andExpect(status().is3xxRedirection());

    }
}