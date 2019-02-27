package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.UserServices.GeraetService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GeraetServiceTest {
    @Mock
    GeraetRepository geraetRepository;
    @Mock
    RentEventRepository rentEventRepository;

    @InjectMocks
    GeraetService geraetService;


    @Test
    public void  get_all_with_bilder(){
        List<Geraet> geraets = new ArrayList<>();

        Geraet fakeGeraet1 = new Geraet();
        List<Bild> bilder1 = new ArrayList<>();
        Bild fakeBild1 = new Bild();
        fakeBild1.setBild(new byte[0]);
        bilder1.add(fakeBild1);
        fakeGeraet1.setBilder(bilder1);

        Geraet fakeGeraet2 = new Geraet();
        List<Bild> bilder2 = new ArrayList<>();
        Bild fakeBild2 = new Bild();
        fakeBild2.setBild("fake".getBytes());
        bilder2.add(fakeBild2);
        fakeGeraet2.setBilder(bilder2);

        geraets.add(fakeGeraet1);
        geraets.add(fakeGeraet2);

        Mockito.when(geraetRepository.findAllByTitelLike(any())).thenReturn(geraets);
        Mockito.when(geraetRepository.findAllByBesitzer(any())).thenReturn(geraets);

        List<Geraet> geraetsWithEncode = geraetService.getAllWithKeyWithBiler("");
        Assertions.assertThat(geraetsWithEncode.get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(geraetsWithEncode.get(1).getEncode()).isNotEqualTo(null);

        geraetsWithEncode = geraetService.getAllByBesitzerWithBilder("");
        Assertions.assertThat(geraetsWithEncode.get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(geraetsWithEncode.get(1).getEncode()).isNotEqualTo(null);
    }

    @Test
    public void testGeraetBilder(){
        Geraet fakeGeraet1 = new Geraet();

        Bild fakeBild1 = new Bild();
        fakeBild1.setBild(new byte[5]);

        List<Bild> bilder1 = new ArrayList<>();
        bilder1.add(fakeBild1);

        fakeGeraet1.setBilder(bilder1);

        Assertions.assertThat(geraetService.geraetBilder(fakeGeraet1).size()).isEqualTo(0);


        Geraet fakeGeraet2 = new Geraet();

        Bild fakeBild2 = new Bild();
        fakeBild2.setBild(new byte[5]);

        List<Bild> bilder2 = new ArrayList<>();
        bilder2.add(fakeBild1);
        bilder2.add(fakeBild2);

        fakeGeraet2.setBilder(bilder2);

        Assertions.assertThat(geraetService.geraetBilder(fakeGeraet2).size()).isEqualTo(1);

    }

    @Test
    public void check_for_touching_intervals(){
        TimeInterval time = new TimeInterval();
        time.setStart(Date.valueOf("2019-2-20"));
        time.setEnd(Date.valueOf("2019-3-10"));
        RentEvent fake = new RentEvent();
        fake.setTimeInterval(time);

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

        geraetService.checkForTouchingIntervals(fakeGeraet,fake);
        verify(geraetRepository,times(1)).save(any());
    }

    @Test
    public void check_Rent_EventStatus(){

        List<RentEvent> rentEventList = new ArrayList<>();
        RentEvent rentEvent1 = new RentEvent();

        TimeInterval timeInterval1 = new TimeInterval();
        timeInterval1.setStart(Date.valueOf("2019-1-10"));
        timeInterval1.setEnd(Date.valueOf("2019-2-10"));
        rentEvent1.setTimeInterval(timeInterval1);
        rentEventList.add(rentEvent1);

        RentEvent rentEvent2 = new RentEvent();
        TimeInterval timeInterval2 = new TimeInterval();
        timeInterval2.setStart(Date.valueOf("2019-2-23"));
        timeInterval2.setEnd(Date.valueOf("2019-4-24"));
        rentEvent2.setTimeInterval(timeInterval2);
        rentEventList.add(rentEvent2);

        RentEvent rentEvent3 = new RentEvent();
        TimeInterval timeInterval3 = new TimeInterval();
        timeInterval3.setStart(Date.valueOf("2019-5-28"));
        timeInterval3.setEnd(Date.valueOf("2019-6-24"));
        rentEvent3.setTimeInterval(timeInterval3);
        rentEventList.add(rentEvent3);
        Mockito.when(rentEventRepository.findAll()).thenReturn(rentEventList);

        geraetService.checkRentEventStatus();
        verify(rentEventRepository,times(4)).save(any());
    }

    @Test
    public void return_geraet_with_rentEvents(){

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

        when(geraetRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Geraet()));
        Assertions.assertThat(geraetService.returnGeraeteWithRentEvents(rentEventList).size()).isEqualTo(3);

    }

//    @Test
//    public void convert_to_GET(){
//        TimeInterval fake = new TimeInterval(Date.valueOf("2019-2-25"),Date.valueOf("2019-2-28"));
//        TimeInterval newfake = geraetService.convertToCET(fake);
//        Assertions.assertThat(fake.getStart()).isEqualTo(new Date(newfake.getStart().getTime() - 60*60*6000));
//        Assertions.assertThat(fake.getEnd()).isEqualTo(new Date(newfake.getEnd().getTime() - 60*60*6000));
//
//    }

}
