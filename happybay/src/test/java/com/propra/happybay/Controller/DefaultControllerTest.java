package com.propra.happybay.Controller;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.DefaultServices.UserValidator;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.NotificationService;
import com.propra.happybay.Service.UserServices.PersonService;
import com.propra.happybay.Service.UserServices.PictureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultControllerTest {
    private Person person = new Person();
    private Account account = new Account();
    private Geraet geraet=new Geraet();
    private List<Geraet> geraetList=new ArrayList<>();

    @MockBean
    private PersonRepository personRepository;
    @MockBean
    GeraetRepository geraetRepository;
    @MockBean
    GeraetService geraetService;
    @MockBean
    PersonService personService;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @MockBean
    BindingResult result;
    @MockBean
    NotificationService notificationService;
    @MockBean
    UserValidator userValidator;
    @MockBean
    PictureService pictureService;

    MultipartFile file=new MultipartFile() {
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



    @Before
    public void setup() throws IOException {
        person.setUsername("testAdmin");
        person.setId(1L);
        person.setAdresse("test dusseldorf");
        person.setPassword(encoder.encode("test"));

        //file
        byte[] bytes=new byte[100];
        new Random().nextBytes(bytes);

        //gerät
        geraet.setId(2L);
        geraetList.add(geraet);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @WithMockUser(value = "test",roles = "USER")
    @Test
    public void index_USER() throws Exception {
        when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        doNothing().when(notificationService).updateAnzahl(any());
        when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.ofNullable(person));
        doNothing().when(geraetService).checkRentEventStatus(any());
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void index_NO_USER() throws Exception {
        when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        doNothing().when(notificationService).updateAnzahl(any());
        when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.ofNullable(person));
        doNothing().when(geraetService).checkRentEventStatus(any());
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test",roles = "USER")
    @Test
    public void addToDatabase_noErrorPassWord() throws Exception {
        doNothing().when(userValidator).validate(any(),any());
        doNothing().when(personService).makeAndSaveNewPerson(any(),any());

        mvc.perform(post("/addNewUser").flashAttr("person",person).requestAttr("file", file))
                .andExpect(status().isOk());
    }
//    @WithMockUser(value = "test",roles = "USER")
//    @Test
//    public void addToDatabase_errorPassword() throws Exception {
//        doNothing().when(userValidator).validate(any(),any());
//        doNothing().when(personService).makeAndSaveNewPerson(any(),any());
//        when(bindingResult.hasErrors()).thenReturn(false);
//        mvc.perform(post("/addNewUser").flashAttr("person",person).requestAttr("file", file))
//                .andExpect(status().isOk());
//    }
    @Test
    public void register() throws Exception {
        mvc.perform(get("/register").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void login() throws Exception {
        mvc.perform(get("/login").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void loginWithError() throws Exception {
        mvc.perform(get("/login").param("error","").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void loginWithLogOut() throws Exception {
        mvc.perform(get("/login").param("logout","").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}