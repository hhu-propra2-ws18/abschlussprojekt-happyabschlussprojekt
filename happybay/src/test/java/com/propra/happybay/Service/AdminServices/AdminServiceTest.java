package com.propra.happybay.Service.AdminServices;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.HelperClassesForViews.InformationForMenuBadges;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Repository.AccountRepository;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
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
    List<Person> personList = new ArrayList<>();
    List<RentEvent> rentEventList = new ArrayList<>();
    Person fakePerson1 = new Person();
    Person fakePerson2 = new Person();
    List<RentEvent> rentEventsWithConflicts = new ArrayList<>();
    @Test
    public void return_information_for_menuBadges() {


        fakePerson1.setUsername("notadmin");
        personList.add(fakePerson1);
        personList.add(fakePerson1);
        personList.add(fakePerson1);

        fakePerson2.setUsername("admin");
        personList.add(fakePerson2);

        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());
        rentEventList.add(new RentEvent());



        when(personRepository.findAll()).thenReturn(personList);
        Account fakeAccount = new Account();
        when(accountRepository.findByAccount(any())).thenReturn(java.util.Optional.ofNullable(fakeAccount));
        when(rentEventRepository.findAllByReturnStatus(any())).thenReturn(rentEventList);

        InformationForMenuBadges info = new InformationForMenuBadges();
        info.setNumberOfConflicts(4);
        info.setNumberOfPersons(3);

        Assertions.assertThat(adminService.returnInformationForMenuBadges()).isEqualTo(info);
    }

    @Test
    public void get_geraet_with_rent_events_with_conflicts(){

        RentEvent fakeRentEvent1 = new RentEvent();
        RentEvent fakeRentEvent2 = new RentEvent();
        RentEvent fakeRentEvent3 = new RentEvent();
        rentEventsWithConflicts.add(fakeRentEvent1);
        rentEventsWithConflicts.add(fakeRentEvent2);
        rentEventsWithConflicts.add(fakeRentEvent3);
        GeraetWithRentEvent test = new GeraetWithRentEvent();
        when(rentEventRepository.findAllByReturnStatus(ReturnStatus.KAPUTT)).thenReturn(rentEventsWithConflicts);
        List<GeraetWithRentEvent> geraetWithRentEventsWithConflicts = adminService.getGeraetWithRentEventsWithConflicts();
        Assertions.assertThat(geraetWithRentEventsWithConflicts.size()).isEqualTo(3);
        test.setRentEvent(fakeRentEvent1);
        Assertions.assertThat(geraetWithRentEventsWithConflicts.get(0)).isEqualTo(test);
        test.setRentEvent(fakeRentEvent2);
        Assertions.assertThat(geraetWithRentEventsWithConflicts.get(1)).isEqualTo(test);
        test.setRentEvent(fakeRentEvent3);
        Assertions.assertThat(geraetWithRentEventsWithConflicts.get(2)).isEqualTo(test);
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
//    @Test
//    public void changeAdminPassword(){
//        AdminService adminService= new AdminService();
//        adminService.changeAdminPassword("");
//
//    }
}