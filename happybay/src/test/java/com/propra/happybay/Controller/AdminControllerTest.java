package com.propra.happybay.Controller;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.TransferRequestRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import com.propra.happybay.Service.AdminServices.AdminServiceImpl;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {
    private Person person = new Person();
    private Account account = new Account();
    private Geraet geraet=new Geraet();
    private PersonMitAccount personMitAccount;
    private TransferRequest transferRequest;
    private List<Geraet> geraetList=new ArrayList<>();
    private List<PersonMitAccount> personMitAccountList = new ArrayList<>();
    private InformationForMenuBadges informationForMenuBadges = new InformationForMenuBadges();
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AccountRepository accountRepository;
    @MockBean
    AdminServiceImpl mockAdminServiceImpl;
    @MockBean
    TransferRequestRepository mockTransferRequestRepository;
    @MockBean
    GeraetRepository geraetRepository;
    @MockBean
    ProPayService proPayService;
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
        informationForMenuBadges.setNumberOfConflicts(1);
        informationForMenuBadges.setNumberOfNotifications(1);
        informationForMenuBadges.setNumberOfPersons(1);
        List<Reservation> reservationList = new ArrayList<>();
        account.setAccount(person.getUsername());
        account.setAmount(100.0);
        account.setReservations(reservationList);
        accountRepository.save(account);
        personMitAccount = new PersonMitAccount(person,account);
        transferRequest = new TransferRequest(person.getUsername(),200);
        //gerät
        geraet.setId(2L);
        geraet.setReturnStatus(ReturnStatus.KAPUTT);
        geraetList.add(geraet);

        personMitAccountList.add(personMitAccount);
        Mockito.when(mockAdminServiceImpl.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void adminFunktion() throws Exception {
        Mockito.when(mockAdminServiceImpl.isAdminHasDefaultPassword()).thenReturn(false);
        mvc.perform(get("/admin/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void adminFunktionReturnChangePassword() throws Exception {
        Mockito.when(mockAdminServiceImpl.isAdminHasDefaultPassword()).thenReturn(true);
        mvc.perform(get("/admin/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void allUsers() throws Exception {
        Mockito.when(mockAdminServiceImpl.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(get("/admin/allUsers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void one_conflicts_test() throws Exception {
        Mockito.when(geraetRepository.findAllByReturnStatus(ReturnStatus.KAPUTT)).thenReturn(geraetList);
        Mockito.when(mockAdminServiceImpl.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(mockAdminServiceImpl.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(get("/admin/conflicts"))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void adminNotifications() throws Exception {
        List<TransferRequest> transferRequestList = new ArrayList<>();
        transferRequestList.add(transferRequest);
        Mockito.when(mockTransferRequestRepository.findAll()).thenReturn(transferRequestList);
        mvc.perform(get("/admin/notifications").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void erhoehe_test() throws Exception {
        Mockito.when(mockAdminServiceImpl.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(mockAdminServiceImpl.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(post("/admin/erhoeheAmount")
                .flashAttr("username","testAdmin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void releaseAccount() throws Exception {
        Mockito.when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
        mvc.perform(post("/admin/releaseAccount").flashAttr("mieter","test").flashAttr("geraetId",2L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void punishAccount() throws Exception {
        Mockito.when(geraetRepository.findById(2L)).thenReturn(Optional.ofNullable(geraet));
        mvc.perform(post("/admin/punishAccount").flashAttr("mieter","test").flashAttr("geraetId",2L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin", roles = "ADMIN")
    @Test
    public void aufladenAntragTest() throws Exception {
        Mockito.when(mockAdminServiceImpl.returnInformationForMenuBadges()).thenReturn(informationForMenuBadges);
        Mockito.when(mockAdminServiceImpl.returnAllPersonsWithAccounts()).thenReturn(personMitAccountList);
        mvc.perform(post("/admin/propay")
                .flashAttr("amount",10)
                .flashAttr("account","testAdmin"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void changePassword() throws Exception {
        mvc.perform(post("/admin/changePassword").flashAttr("newPassword","testAdmin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void erhoehungGenehmigen() throws Exception {
        mvc.perform(post("/admin/erhoehungGenehmigen").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
    @WithMockUser(value = "testAdmin",password = "test",roles = "ADMIN")
    @Test
    public void erhoehungAblehnen() throws Exception {
        mvc.perform(post("/admin/erhoehungAblehenen").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }



}