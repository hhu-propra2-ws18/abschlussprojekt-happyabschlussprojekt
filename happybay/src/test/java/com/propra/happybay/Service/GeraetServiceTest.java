package com.propra.happybay.Service;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.UserServices.GeraetService;
import com.propra.happybay.Service.UserServices.PersonService;
import com.propra.happybay.Service.UserServices.PictureService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Mock
    PersonService personService;

    @InjectMocks
    GeraetService geraetService;


    @Test
    public void  get_all_with_bilder(){
        Geraet fakeGeraet1 = new Geraet();
        List<Bild> bilder1 = new ArrayList<>();
        Bild fakeBild1 = new Bild();
        fakeBild1.setBild(new byte[0]);
        bilder1.add(fakeBild1);
        fakeGeraet1.setBilder(bilder1);
        fakeGeraet1.setKosten(100);
        fakeGeraet1.setLikes(100);

        Geraet fakeGeraet2 = new Geraet();
        List<Bild> bilder2 = new ArrayList<>();
        Bild fakeBild2 = new Bild();
        fakeBild2.setBild("fake".getBytes());
        bilder2.add(fakeBild2);
        fakeGeraet2.setBilder(bilder2);
        fakeGeraet2.setKosten(10);
        fakeGeraet2.setLikes(1000);

        List<Geraet> geraets = new ArrayList<>();
        geraets.add(fakeGeraet1);
        geraets.add(fakeGeraet2);
        Mockito.when(geraetRepository.findAllByTitelLike(any())).thenReturn(geraets);
        Mockito.when(geraetRepository.findAllByBesitzer(any(Person.class))).thenReturn(geraets);

        List<Geraet> geraetSortmitaufsteigenderPreis = new ArrayList<>();
        geraetSortmitaufsteigenderPreis.add(fakeGeraet2);
        geraetSortmitaufsteigenderPreis.add(fakeGeraet1);
        Person besitzer = new Person();
        Mockito.when(geraetRepository.findAllByTitelLikeOrderByKostenAsc(any())).thenReturn(geraetSortmitaufsteigenderPreis);

        List<Geraet> geraetSortmitabsteigenderPreis = new ArrayList<>();
        geraetSortmitabsteigenderPreis.add(fakeGeraet1);
        geraetSortmitabsteigenderPreis.add(fakeGeraet2);
        Mockito.when(geraetRepository.findAllByTitelLikeOrderByKostenDesc(any())).thenReturn(geraetSortmitabsteigenderPreis);

        List<Geraet> geraetSortmitaufsteigenderLike = new ArrayList<>();
        geraetSortmitaufsteigenderLike.add(fakeGeraet1);
        geraetSortmitaufsteigenderLike.add(fakeGeraet2);
        Mockito.when(geraetRepository.findAllByTitelLikeOrderByLikesAsc(any())).thenReturn(geraetSortmitaufsteigenderLike);

        List<Geraet> geraetSortmitabsteigenderLike = new ArrayList<>();
        geraetSortmitabsteigenderLike.add(fakeGeraet2);
        geraetSortmitabsteigenderLike.add(fakeGeraet1);
        Mockito.when(geraetRepository.findAllByTitelLikeOrderByLikesDesc(any())).thenReturn(geraetSortmitabsteigenderLike);

        List<Geraet> geraetsWithEncode = geraetService.getAllWithKeyWithBiler("");
        Assertions.assertThat(geraetsWithEncode.get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(geraetsWithEncode.get(1).getEncode()).isNotEqualTo(null);

        geraetsWithEncode = geraetService.getAllByBesitzerWithBilder(besitzer);
        Assertions.assertThat(geraetsWithEncode.get(0).getEncode()).isEqualTo(null);
        Assertions.assertThat(geraetsWithEncode.get(1).getEncode()).isNotEqualTo(null);

        geraetsWithEncode = geraetService.getAllWithFilterPreisAufsteigendWithBilder("");
        Assertions.assertThat(geraetsWithEncode.get(0).getKosten()).isEqualTo(10);
        Assertions.assertThat(geraetsWithEncode.get(1).getKosten()).isEqualTo(100);

        geraetsWithEncode = geraetService.getAllWithFilterPreisAbsteigendWithBilder("");
        Assertions.assertThat(geraetsWithEncode.get(0).getKosten()).isEqualTo(100);
        Assertions.assertThat(geraetsWithEncode.get(1).getKosten()).isEqualTo(10);

        geraetsWithEncode = geraetService.getAllWithFilterLikeAufsteigendWithBilder("");
        Assertions.assertThat(geraetsWithEncode.get(0).getLikes()).isEqualTo(100);
        Assertions.assertThat(geraetsWithEncode.get(1).getLikes()).isEqualTo(1000);

        geraetsWithEncode = geraetService.getAllWithFilterLikeAbsteigendWithBilder("");
        Assertions.assertThat(geraetsWithEncode.get(0).getLikes()).isEqualTo(1000);
        Assertions.assertThat(geraetsWithEncode.get(1).getLikes()).isEqualTo(100);

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

        Assertions.assertThat(geraetService.returnGeraeteWithRentEvents(rentEventList).size()).isEqualTo(3);

    }

    @Test
    public void convert_to_CET(){
        TimeInterval fake = new TimeInterval(Date.valueOf("2019-2-25"),Date.valueOf("2019-2-28"));
        TimeInterval newfake = geraetService.convertToCET(fake);
        Assertions.assertThat(fake.getStart()).isEqualTo(new Date(newfake.getStart().getTime() - 60*60*6000));
        Assertions.assertThat(fake.getEnd()).isEqualTo(new Date(newfake.getEnd().getTime() - 60*60*6000));

    }

    @Test
    public void save_geraet() throws IOException {
        MultipartFile fakefile = mock(MultipartFile.class);
        MultipartFile[] files = {fakefile,fakefile};

        Geraet fakegeraet = new Geraet();
        fakegeraet.setTitel("fake geraet");
        fakegeraet.setKosten(1);
        fakegeraet.setBeschreibung("fake Beschreibung");
        fakegeraet.setKaution(1);
        fakegeraet.setAbholort("fake Ort");
        when(geraetRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(fakegeraet));
        geraetService.editGeraet(files,fakegeraet,1L,true);

    }

    @Test
    public void add_like(){
        Geraet fakegeraet = new Geraet();
        List<Person> fakeLiekedPerson = new ArrayList<>();
        Person fakePerson1 = new Person();
        fakePerson1.setUsername("fake Person1");
        Person fakePerson2 = new Person();
        fakePerson2.setUsername("fake Person2");
        Person fakePerson3 = new Person();
        fakePerson3.setUsername("fake Person3");
        fakeLiekedPerson.add(fakePerson1);
        fakeLiekedPerson.add(fakePerson2);
        fakegeraet.setLikedPerson(fakeLiekedPerson);
        fakegeraet.setLikes(2);
        Mockito.when(geraetRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(fakegeraet));
        geraetService.addLike(1L,fakePerson1);
        geraetService.addLike(1L,fakePerson3);
        verify(geraetRepository,times(2)).save(any());
    }
}
