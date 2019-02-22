package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
//
import com.propra.happybay.Repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Optional;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @MockBean
    PersonRepository personRepository;
    private Person person=new Person();
    private Geraet geraet=new Geraet();
    private List<Geraet> geraetList=new ArrayList<>();
    private List<PersonMitAccount> personMitAccountList=new ArrayList<>();
    @Autowired
    WebApplicationContext context;
    @MockBean
    AdminService adminService;
    @MockBean
    GeraetRepository geraetRepository;
    private MockMvc mvc;
    private InformationForMenuBadges informationForMenuBadges=new InformationForMenuBadges();



    @Before
    public void setup() throws IOException {
        person.setUsername("testAdmin");
        person.setId(1L);
        person.setAdresse("test dusseldorf");
//gerät
        geraet.setId(1L);
        geraet.setReturnStatus(ReturnStatus.KAPUTT);
        geraetList.add(geraet);
        Account account=new Account();
        account.setAccount("test");
        account.setAmount(1.0);
//person mit Account
        PersonMitAccount personMitAccount=new PersonMitAccount(person,account);
        personMitAccountList.add(personMitAccount);
//informationForMenuBadges
        informationForMenuBadges.setNumberOfPersons(1);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void adminFunktion_DefaultPassword_test() throws Exception {
        Mockito.when(adminService.isAdminHasDefaultPassword()).thenReturn(false);
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);

        PersonRepository userRepoFromContext = context.getBean(PersonRepository.class);

        mvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
        Mockito.verify(personRepository).count();
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void adminFunktion_NotDefaultPassword_test() throws Exception {
        Mockito.when(adminService.isAdminHasDefaultPassword()).thenReturn(true);
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);


        mvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void allUser_test() throws Exception {
        Mockito.when(adminService.isAdminHasDefaultPassword()).thenReturn(true);
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);


        mvc.perform(get("/admin/allUsers"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void one_conflicts_test() throws Exception {
        Mockito.when(geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT)).thenReturn(geraetList);
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(get("/admin/conflicts"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void erhoehe_test() throws Exception {
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(post("/admin/erhoeheAmount")
        .flashAttr("username","testAdmin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void release_test() throws Exception {
        Mockito.when(adminService.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(adminService.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(post("/admin/erhoeheAmount")
                .flashAttr("username","testAdmin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}


//
//@RunWith(SpringRunner.class)

//public class AdminControllerTest {
//    private MultipartFile file = new MultipartFile() {
//        @Override
//        public String getName() {
//            return null;
//        }
//
//        @Override
//        public String getOriginalFilename() {
//            return null;
//        }
//
//        @Override
//        public String getContentType() {
//            return null;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return false;
//        }
//
//        @Override
//        public long getSize() {
//            return 0;
//        }
//
//        @Override
//        public byte[] getBytes() throws IOException {
//            return new byte[0];
//        }
//
//        @Override
//        public InputStream getInputStream() throws IOException {
//            return null;
//        }
//
//        @Override
//        public void transferTo(File dest) throws IOException, IllegalStateException {
//
//        }
//    };
//    private Person person = new Person();
//    @Autowired
//    private WebApplicationContext context;
//    @Autowired
//    public PasswordEncoder encoder;
//
//    private MockMvc mvc;
//
//    @Autowired
//    PersonRepository personRepository;
//

//
//
//
//    @Test
//    public void proflie() throws Exception {
//        mvc.perform(get("/admin").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//    @WithMockUser(value = "testAdmin", roles = "ADMIN")
//    @Test
//    public void conflicts() throws Exception {
//        mvc.perform(get("/admin/conflicts").contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//}