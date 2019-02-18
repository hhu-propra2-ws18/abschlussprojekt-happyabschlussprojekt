package propra2.person.Service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import propra2.person.Model.Person;
import propra2.person.Model.PersonMitProjekten;
import propra2.person.Model.Projekt;
import propra2.person.Repository.PersonRepository;
import propra2.person.Repository.ProjektRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonenMitProjektenServiceTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    ProjektRepository projektRepository;
    @InjectMocks
    PersonenMitProjektenService personenMitProjektenService;


    private Person one = new Person();
    private Person two = new Person();
    private Projekt oneProjekt= new Projekt();
    private Projekt twoProjekt= new Projekt();
    private List<Projekt> projektList1= new ArrayList<>();
    private List<Projekt> projektList2= new ArrayList<>();

    private PersonMitProjekten personMitProjekten1=new PersonMitProjekten();
    private PersonMitProjekten personMitProjekten2=new PersonMitProjekten();
    private List<PersonMitProjekten> personMitProjektenList=new ArrayList<>();


    @Test
    public void Fall_2_Person_Mit_Projekten(){
        List<Person> personList=new ArrayList<>();
        one.setVorname("one");
        two.setVorname("two");

        oneProjekt.setId(1L);
        one.setProjekteId(new Long[]{1L});
        twoProjekt.setId(2L);
        two.setProjekteId(new Long[]{2L});

        personList.add(one);
        personList.add(two);


        when(personRepository.findAll()).thenReturn(personList);


        when(projektRepository.findAllById(1L)).thenReturn(oneProjekt);
        when(projektRepository.findAllById(2L)).thenReturn(twoProjekt);

        projektList1.add(oneProjekt);
        projektList2.add(twoProjekt);

        personMitProjekten1.setPerson(one);
        personMitProjekten1.setProjekte(projektList1);
        personMitProjekten2.setPerson(two);
        personMitProjekten2.setProjekte(projektList2);
        personMitProjektenList.add(personMitProjekten1);
        personMitProjektenList.add(personMitProjekten2);
        List<PersonMitProjekten> expected= personMitProjektenList;
        List<PersonMitProjekten> actual= personenMitProjektenService.returnPersonenMitProjekten();
        assertEquals(actual, expected);
        verify(projektRepository,times(2)).findAllById(anyLong());
        verify(personRepository,times(1)).findAll();

    }
    @Test
    public void Fall_2_Person_Ohne_Projekten(){
        List<Person> personList=new ArrayList<>();
        one.setVorname("one");
        two.setVorname("two");

        oneProjekt.setId(1L);

        twoProjekt.setId(2L);

        when(personRepository.findAll()).thenReturn(personList);




        personList.add(one);
        personList.add(two);
        personMitProjekten1.setPerson(one);
        personMitProjekten1.setProjekte(projektList1);
        personMitProjekten2.setPerson(two);
        personMitProjekten2.setProjekte(projektList2);
        personMitProjektenList.add(personMitProjekten1);
        personMitProjektenList.add(personMitProjekten2);
        List<PersonMitProjekten> expected= personMitProjektenList;
        List<PersonMitProjekten> actual= personenMitProjektenService.returnPersonenMitProjekten();
        assertEquals(expected,actual);
        verify(projektRepository,times(0)).findAllById(anyLong());
        verify(personRepository,times(1)).findAll();

    }
}