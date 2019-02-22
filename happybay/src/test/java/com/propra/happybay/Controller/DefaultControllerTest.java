package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.TransferRequestRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminServiceImpl;
import com.propra.happybay.Service.GeraetService;
import com.propra.happybay.Service.ProPayService;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
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
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;
    @Before
    public void setup() throws IOException {
        person.setUsername("testAdmin");
        person.setId(1L);
        person.setAdresse("test dusseldorf");
        person.setPassword(encoder.encode("test"));
        personRepository.save(person);

        //gerät
        geraet.setId(2L);
        geraet.setReturnStatus(ReturnStatus.KAPUTT);
        geraetList.add(geraet);

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @WithMockUser(value = "test",roles = "USER")
    @Test
    public void index_USER() throws Exception {
        Mockito.when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }
    @Test
    public void index_NO_USER() throws Exception {
        Mockito.when(geraetService.getAllWithKeyWithBiler(any())).thenReturn(geraetList);
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    public void addToDatabase() throws Exception {
        mvc.perform(post("/addNewUser")
                .flashAttr("person",person)
                .param("file"))
                .andExpect(status().isOk());
    }

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