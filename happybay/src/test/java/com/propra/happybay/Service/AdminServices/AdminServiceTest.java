//package com.propra.happybay.Service.AdminServices;
//
//import com.propra.happybay.Model.Account;
//import com.propra.happybay.Model.Geraet;
//import com.propra.happybay.Model.Person;
//import com.propra.happybay.Repository.AccountRepository;
//import com.propra.happybay.Repository.GeraetRepository;
//import com.propra.happybay.Repository.PersonRepository;
//import com.propra.happybay.Repository.TransferRequestRepository;
//import com.propra.happybay.Service.AdminServices.AdminServiceImpl;
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestContext;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
//@WebAppConfiguration
//public class AdminServiceTest {
//    @Mock
//    PersonRepository personRepository;
//    @Mock
//    AccountRepository accountRepository;
//    @Mock
//    GeraetRepository geraetRepository;
//    @Mock
//    TransferRequestRepository transferRequestRepository;
//    @Mock
//    PasswordEncoder encoder;
//
//    @InjectMocks
//    AdminServiceImpl adminService;
//
//
//    @Test
//    public void return_information_for_menuBadges() {
//        List<Person> personList = new ArrayList<>();
//        Person fakePerson1 = new Person();
//        fakePerson1.setUsername("notadmin");
//        personList.add(fakePerson1);
//        personList.add(fakePerson1);
//        personList.add(fakePerson1);
//        Person fakePerson2 = new Person();
//        fakePerson2.setUsername("admin");
//        personList.add(fakePerson2);
//
//        List<Geraet> geraetList = new ArrayList<>();
//        geraetList.add(new Geraet());
//        geraetList.add(new Geraet());
//        geraetList.add(new Geraet());
//        geraetList.add(new Geraet());
//
//        List<TransferRequest> transferRequestList = new ArrayList<>();
//        transferRequestList.add(new TransferRequest());
//        transferRequestList.add(new TransferRequest());
//        transferRequestList.add(new TransferRequest());
//        transferRequestList.add(new TransferRequest());
//        transferRequestList.add(new TransferRequest());
//
//
//        Mockito.when(personRepository.findAll()).thenReturn(personList);
//        Account fakeAccount = new Account();
//        Mockito.when(accountRepository.findByAccount(any())).thenReturn(java.util.Optional.ofNullable(fakeAccount));
//        Mockito.when(geraetRepository.findAllByReturnStatus(any())).thenReturn(geraetList);
//        Mockito.when(transferRequestRepository.findAll()).thenReturn(transferRequestList);
//
//        InformationForMenuBadges info = new InformationForMenuBadges();
//        info.setNumberOfConflicts(4);
//        info.setNumberOfPersons(3);
//        info.setNumberOfNotifications(5);
//
//        Assertions.assertThat(adminService.returnInformationForMenuBadges()).isEqualTo(info);
//    }
//}