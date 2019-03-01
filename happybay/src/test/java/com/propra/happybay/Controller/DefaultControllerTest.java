package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.DefaultServices.UserValidator;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import com.propra.happybay.Service.UserServices.PictureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class DefaultControllerTest {
    private Person person = new Person();
    private Account account = new Account();
    private Geraet geraet = new Geraet();
    private List<Geraet> geraetList = new ArrayList<>();
    private RentEvent rentEvent = new RentEvent();
    private List<RentEvent> verfuegbareEvents = new ArrayList<>();
    Date start = new Date(2019, 10, 20);
    Date end = new Date(2019, 11, 21);
    private TimeInterval timeInterval = new TimeInterval(start, end);
    @Mock
    private PersonRepository personRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    GeraetService geraetService;
    @Mock
    PersonService personService;
    @Autowired
    public PasswordEncoder encoder;
    private MockMvc mvc;
    @Mock
    RentEventRepository rentEventRepository;
    @Mock
    NotificationService notificationService;
    @Mock
    UserValidator userValidator;
    @Mock
    PictureService pictureService;
    Principal principal = new Principal() {
        @Override
        public String getName() {
            return "test";
        }
    };
    private final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.png");
    private final MockMultipartFile file = new MockMultipartFile("test.png", "test.png", "image/png", inputStream);

    public DefaultControllerTest() throws IOException {
    }

    @Before
    public void setup() throws IOException {
        person.setUsername("testAdmin");
        person.setId(1L);
        person.setAdresse("test dusseldorf");

        //file
        byte[] bytes = new byte[100];
        new Random().nextBytes(bytes);

        //gerät
        geraet.setId(2L);
        geraetList.add(geraet);

        //rentEvent.setGeraetId(2L);
        rentEvent.setMieter(person);
        rentEvent.setTimeInterval(timeInterval);
        verfuegbareEvents.add(rentEvent);

        when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        doNothing().when(notificationService).updateAnzahlOfNotifications(any());
        when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.ofNullable(person));
        when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        when(rentEventRepository.findAllByMieterAndReturnStatus(any(), any())).thenReturn(verfuegbareEvents);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");
        mvc = MockMvcBuilders.standaloneSetup(new DefaultController(userValidator, rentEventRepository, geraetService, personService, personRepository, geraetRepository, notificationService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void index_USER_no_key() throws Exception {
        mvc.perform(get("/").param("key", "").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void index_USER_preisAufsteigend() throws Exception {
        mvc.perform(get("/").param("key", "preisAufsteigend").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void index_USER_preisAbsteigend() throws Exception {
        mvc.perform(get("/").param("key", "preisAbsteigend").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void index_USER_likeAufsteigend() throws Exception {
        mvc.perform(get("/").param("key", "likeAufsteigend").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void index_USER_likeAbsteigend() throws Exception {
        mvc.perform(get("/").param("key", "likeAbsteigend").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void index_NO_USER() throws Exception {
        when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void addToDatabase_noErrorPassWord() throws Exception {
        doNothing().when(userValidator).validate(any(), any());
        mvc.perform(post("/addNewUser").principal(principal).flashAttr("person", person).requestAttr("file", file))
                .andExpect(status().isOk());
        verify(userValidator, Mockito.times(1)).validate(any(), any());

    }

    @Test
    public void register() throws Exception {
        mvc.perform(get("/register").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void login() throws Exception {
        mvc.perform(get("/login").principal(principal))
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithError() throws Exception {
        mvc.perform(get("/login").principal(principal).param("error", ""))
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithLogOut() throws Exception {
        mvc.perform(get("/login").principal(principal).param("logout", ""))
                .andExpect(status().isOk());
    }

}