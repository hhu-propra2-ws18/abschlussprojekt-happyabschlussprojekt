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
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Date;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private Person user = new Person();
    private Person admin = new Person();
    private Geraet geraet = new Geraet();
    private Account account = new Account();
    Date start = new Date(2019,10,20);
    Date end = new Date(2019,11,21);
    private TimeInterval timeInterval = new TimeInterval(start,end);
    //Bild
    private Bild bild = new Bild();
    private RentEvent verfuerbar = new RentEvent();
    private MultipartFile files = new MultipartFile() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOriginalFilename() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return new byte[0];
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    @Autowired
    private WebApplicationContext context;
    @Autowired
    public PasswordEncoder encoder;

    private MockMvc mvc;

    @Autowired
    PersonRepository personRepository;
    @MockBean
    GeraetRepository geraetRepository;
    @MockBean
    AccountRepository accountRepository;
    @MockBean
    NotificationService notificationService;
    private Notification notification=new Notification();
    @MockBean
    MailService mailService;
    @MockBean
    PersonService personService;
    @MockBean
    GeraetService geraetService;
    @Autowired
    RentEventRepository rentEventRepository;
    @Before
    public void setup() throws IOException {
        byte[] bytes = new byte[20];
        new Random().nextBytes(bytes);
        bild.setBild(bytes);
        //User
        user.setUsername("test");
        user.setId(1L);
        user.setFoto(bild);
        user.setRole("ROLE_USER");
        user.setAdresse("test dusseldorf");
        personRepository.save(user);
        //Admin
        admin.setUsername("admin");
        admin.setId(2L);
        admin.setFoto(bild);
        admin.setRole("ROLE_ADMIN");
        personRepository.save(admin);
        //Geraet
        geraet.setTitel("Das ist ein Test");
        geraet.setId(2L);
        geraet.setBesitzer(user.getUsername());
        geraet.setKosten(3);
        geraet.setKaution(10);
        geraet.setMietezeitpunktEnd(end);
        geraet.setMietezeitpunktStart(start);
        geraetRepository.save(geraet);

        //Account
        account.setAccount(user.getUsername());
        account.setAmount(100.0);
        //
        notification.setGeraetId(3L);


        notification.setMietezeitpunktStart(start);
        notification.setMietezeitpunktEnd(end);
//        notification.setMietezeitpunktStart((java.sql.Date) new Date(startMieteZeitpunkt.getTime() + 60*60*1000));
//        notification.setMietezeitpunktEnd((java.sql.Date) new Date(endeMieteZeitpunkt.getTime() + 60*60*1000));
        //
        verfuerbar.setTimeInterval(geraetService.convertToCET(timeInterval));
        geraet.getVerfuegbareEvents().add(verfuerbar);

//        RentEvent rentEvent = new RentEvent();
//        rentEvent.setId(1L);
//        rentEvent.setGeraetId(geraet.getId());
//        rentEventRepository.save(rentEvent);

        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .dispatchOptions(true)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void proflieWithUser() throws Exception {
        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
//    @WithMockUser(value = "admin", roles = "ADMIN")
//    @Test
//    public void proflieWithAdmin() throws Exception {
//        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is3xxRedirection());
//    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void myThings() throws Exception {
        mvc.perform(get("/user/myThings").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void rentThings() throws Exception {
        mvc.perform(get("/user/rentThings").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
//    @WithMockUser(value = "test", roles = "USER")
//    @Test
//    public void makeNotifications() throws Exception {
//        mvc.perform(get("/user/notifications").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void anfragenGet() throws Exception {
       when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
       when(accountRepository.findByAccount(user.getUsername())).thenReturn(Optional.ofNullable(account));
        mvc.perform(get("/user/anfragen/{id}",2L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void anfragenPost() throws Exception {

        when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
        doNothing().when(mailService).sendAnfragMail(any(),any(),any());
        mvc.perform(post("/user/anfragen/{id}",2L)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .flashAttr("notification",notification))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void addGeraetGet() throws Exception {
        mvc.perform(get("/user/addGeraet").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void addGeraetPost() throws Exception {

        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.png");
        final MockMultipartFile mockMultipartFile = new MockMultipartFile("test.png", "test.png", "image/png", inputStream);

        MultipartFile[] multipartFiles = new MultipartFile[1];
        multipartFiles[0] = mockMultipartFile;
        mvc.perform(post("/user/addGeraet").flashAttr("geraet", geraet).requestAttr("files",multipartFiles).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void proPay() throws Exception {
        when(accountRepository.findByAccount(any())).thenReturn(Optional.ofNullable(account));
        mvc.perform(get("/user/proPay").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void aufladenAntrag() throws Exception {
        mvc.perform(post("/user/propayErhoehung").flashAttr("amount",30).flashAttr("account","test").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void besitzerInfo() throws Exception {
        mvc.perform(get("/user/BesitzerInfo/{id}",1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
//    @WithMockUser(value = "test", roles = "USER")
//    @Test
//    public void geraetZurueck() throws Exception {
//        mvc.perform(get("/user/geraet/zurueckgeben/{id}",1L).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

}