package com.propra.happybay.Service.AdminServices;

import ch.qos.logback.classic.boolex.GEventEvaluator;
import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.AdminServices.AdminService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class AdminServiceTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    PasswordEncoder encoder;
    @Mock
    RentEventRepository rentEventRepository;

    @InjectMocks
    AdminService adminService;


    @Test
    public void return_information_for_menuBadges() {
        List<Person> personList = new ArrayList<>();
        Person fakePerson1 = new Person();
        fakePerson1.setUsername("notadmin");
        personList.add(fakePerson1);
        personList.add(fakePerson1);
        personList.add(fakePerson1);
        Person fakePerson2 = new Person();
        fakePerson2.setUsername("admin");
        personList.add(fakePerson2);

        List<RentEvent> rentEventList = new ArrayList<>();
        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());



        Mockito.when(personRepository.findAll()).thenReturn(personList);
        Account fakeAccount = new Account();
        Mockito.when(accountRepository.findByAccount(any())).thenReturn(java.util.Optional.ofNullable(fakeAccount));
        Mockito.when(rentEventRepository.findAllByReturnStatus(any())).thenReturn(rentEventList);

        InformationForMenuBadges info = new InformationForMenuBadges();
        info.setNumberOfConflicts(4);
        info.setNumberOfPersons(3);

        Assertions.assertThat(adminService.returnInformationForMenuBadges()).isEqualTo(info);
    }

    @Test
    public void get_geraet_with_rent_events_with_conflicts(){
        List<RentEvent> rentEventsWithConflicts = new ArrayList<>();
        RentEvent fakeRentEvent1 = new RentEvent();
        fakeRentEvent1.setGeraetId(1L);
        RentEvent fakeRentEvent2 = new RentEvent();
        fakeRentEvent2.setGeraetId(2L);
        RentEvent fakeRentEvent3 = new RentEvent();
        fakeRentEvent3.setGeraetId(3L);
        rentEventsWithConflicts.add(fakeRentEvent1);
        rentEventsWithConflicts.add(fakeRentEvent2);
        rentEventsWithConflicts.add(fakeRentEvent3);
        Mockito.when(rentEventRepository.findAllByReturnStatus(any())).thenReturn(rentEventsWithConflicts);
        Geraet fakeGeraet = new Geraet();
        Mockito.when(geraetRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(fakeGeraet));

        Assertions.assertThat(adminService.getGeraetWithRentEventsWithConflicts().size()).isEqualTo(3);
    }
//
//        @Test
//    public void is_admin_default_password(){
//        Person admin = new Person();
//        admin.setUsername("admin");
//        admin.setPassword(encoder.encode("admin"));
//        Mockito.when(personRepository.findByUsername("admin")).thenReturn(java.util.Optional.ofNullable(admin));
//        Assertions.assertThat(adminService.isAdminHasDefaultPassword()).isEqualTo(true);
//
//    }
}