package com.propra.happybay.Service;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.UserServices.PersonService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    RentEventRepository rentEventRepository;
    @Mock
    public PasswordEncoder encoder;
    @Mock
    private ProPayService proPayService;

    @InjectMocks
    PersonService personService;

    @Test
    public void get_by_username(){
        Person fakePerson = new Person();
        fakePerson.setUsername("fake");
        Mockito.when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.ofNullable(fakePerson));
        Assertions.assertThat(personService.getByUsername("fake")).isEqualTo(fakePerson);

    }


    @Test
    public void increase_aktion_punkt(){
        Person fakePerson = new Person();
        fakePerson.setUsername("fake");
        Mockito.when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.ofNullable(fakePerson));

        personService.increaseAktionPunkte("");
        verify(personRepository,times(1)).save(any());

    }

    @Test
    public void position_of_free_block(){

        TimeInterval time1 = new TimeInterval();
        time1.setStart(Date.valueOf("2019-3-20"));
        time1.setEnd(Date.valueOf("2019-4-10"));
        RentEvent fakeAnfragenRentEvent1 = new RentEvent();
        fakeAnfragenRentEvent1.setTimeInterval(time1);

        TimeInterval time2 = new TimeInterval();
        time2.setStart(Date.valueOf("2019-4-11"));
        time2.setEnd(Date.valueOf("2019-5-3"));
        RentEvent fakeAnfragenRentEvent2 = new RentEvent();
        fakeAnfragenRentEvent2.setTimeInterval(time2);

        List<RentEvent> rentEventList = new ArrayList<>();
        RentEvent rentEvent1 = new RentEvent();
        TimeInterval timeInterval1 = new TimeInterval();
        timeInterval1.setStart(Date.valueOf("2019-1-10"));
        timeInterval1.setEnd(Date.valueOf("2019-3-10"));
        rentEvent1.setTimeInterval(timeInterval1);
        rentEventList.add(rentEvent1);

        RentEvent rentEvent2 = new RentEvent();
        TimeInterval timeInterval2 = new TimeInterval();
        timeInterval2.setStart(Date.valueOf("2019-3-10"));
        timeInterval2.setEnd(Date.valueOf("2019-4-24"));
        rentEvent2.setTimeInterval(timeInterval2);
        rentEventList.add(rentEvent2);

        RentEvent rentEvent3 = new RentEvent();
        TimeInterval timeInterval3 = new TimeInterval();
        timeInterval3.setStart(Date.valueOf("2019-5-28"));
        timeInterval3.setEnd(Date.valueOf("2019-6-24"));
        rentEvent3.setTimeInterval(timeInterval3);
        rentEventList.add(rentEvent3);

        Geraet fakeGeraet = new Geraet();
        fakeGeraet.setVerfuegbareEvents(rentEventList);


        Assertions.assertThat(personService.positionOfFreeBlock(fakeGeraet,fakeAnfragenRentEvent1)).isEqualTo(1);
        Assertions.assertThat(personService.positionOfFreeBlock(fakeGeraet,fakeAnfragenRentEvent2)).isEqualTo(-1);

    }

    @Test
    public void interval_zerlagen(){
        TimeInterval time = new TimeInterval();
        time.setStart(Date.valueOf("2019-3-20"));
        time.setEnd(Date.valueOf("2019-4-10"));
        RentEvent fakeAnfragenRentEvent = new RentEvent();
        fakeAnfragenRentEvent.setTimeInterval(time);

        List<RentEvent> rentEventList = new ArrayList<>();
        RentEvent rentEvent = new RentEvent();
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.setStart(Date.valueOf("2019-1-10"));
        timeInterval.setEnd(Date.valueOf("2019-10-10"));
        rentEvent.setTimeInterval(timeInterval);
        rentEventList.add(rentEvent);

        Geraet fakeGeraet = new Geraet();
        fakeGeraet.setVerfuegbareEvents(rentEventList);

        personService.intervalZerlegen(fakeGeraet,0,fakeAnfragenRentEvent);
        verify(geraetRepository,times(1)).save(any());
    }


    @Test
    public void make_comment(){
        Person fakePerson = new Person();
        Person fakeBesitzer = new Person();
        fakeBesitzer.setUsername("fake Besitzer");
        Geraet fakegeraet = new Geraet();
        fakegeraet.setTitel("fake geraet");
        fakegeraet.setBesitzer("fake Besitzer");

        when(personRepository.findByUsername("fake Besitzer")).thenReturn(java.util.Optional.ofNullable(fakeBesitzer));
        personService.makeComment(fakegeraet,fakePerson,"fake grund");
        verify(personRepository,times(1)).save(any());
    }

    @Test
    public void make_and_save_new_person() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        Person fakePerson = new Person();
        fakePerson.setUsername("fake Person");
        fakePerson.setPassword("fakePassword");
        when(file.getBytes()).thenReturn("fake".getBytes());
        personService.makeAndSaveNewPerson(file,fakePerson);
        verify(encoder,times(1)).encode("fakePassword");
        verify(personRepository,times(1)).save(any());
        verify(proPayService,times(1)).saveAccount("fake Person");
    }

    @Test
    public void checks_active_or_in_active_rent_event(){

        List<RentEvent> rentEventList = new ArrayList<>();
        RentEvent rentEvent1 = new RentEvent();
        TimeInterval timeInterval1 = new TimeInterval();
        timeInterval1.setStart(Date.valueOf("2019-1-10"));
        timeInterval1.setEnd(Date.valueOf("2019-3-10"));
        rentEvent1.setTimeInterval(timeInterval1);
        rentEvent1.setGeraetId(1L);
        rentEventList.add(rentEvent1);

        RentEvent rentEvent2 = new RentEvent();
        TimeInterval timeInterval2 = new TimeInterval();
        timeInterval2.setStart(Date.valueOf("2019-3-10"));
        timeInterval2.setEnd(Date.valueOf("2019-4-24"));
        rentEvent2.setTimeInterval(timeInterval2);
        rentEvent2.setGeraetId(2L);
        rentEventList.add(rentEvent2);

        RentEvent rentEvent3 = new RentEvent();
        TimeInterval timeInterval3 = new TimeInterval();
        timeInterval3.setStart(Date.valueOf("2019-5-28"));
        timeInterval3.setEnd(Date.valueOf("2019-6-24"));
        rentEvent3.setTimeInterval(timeInterval3);
        rentEvent3.setGeraetId(3L);
        rentEventList.add(rentEvent3);

        List<GeraetWithRentEvent> geraetWithRentEvents = new ArrayList<>();

        Mockito.when(geraetRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Geraet()));
        personService.checksActiveOrInActiveRentEvent(rentEventList,geraetWithRentEvents);
        verify(geraetRepository,times(3)).findById(anyLong());
    }

}
