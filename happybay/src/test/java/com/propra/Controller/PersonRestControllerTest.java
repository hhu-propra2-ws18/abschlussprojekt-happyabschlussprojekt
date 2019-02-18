package propra2.person.Controller;

import org.junit.Before;
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
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import propra2.person.Model.Person;
import propra2.person.Model.PersonApi;
import propra2.person.Model.PersonEvent;
import propra2.person.Repository.EventRepository;
import propra2.person.Repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonRestControllerTest {
    @Mock
    PersonRepository personRepository;
    @Mock
    EventRepository eventRepository;
    @InjectMocks
    PersonRestController personRestController=new PersonRestController();

    private Person person1 =new Person();
    private Person person2 =new Person();
    private List<Person> personList=new ArrayList<>();
    private Optional<Person> OptionalPerson1 =Optional.ofNullable(person1);
    private Optional<Person> OptionalPerson2 =Optional.ofNullable(person2);

    private List<PersonEvent> personEventList=new ArrayList<>();
    @Before
    public void setUp() {
        person1.setVorname("Tom");

        OptionalPerson1.get().setNachname("Stark");
        OptionalPerson1.get().setJahreslohn("10000");
        OptionalPerson1.get().setKontakt("tung@gmail.com");
        OptionalPerson1.get().setSkills(new String[]{"Java", "Python"});
        OptionalPerson1.get().setId(2L);
        when(personRepository.findById(2L)).thenReturn(OptionalPerson1);

        person1.setVorname("Flo");
        personList.add(person1);
        personList.add(person2);
        OptionalPerson1.get().setNachname("Gre");
        OptionalPerson1.get().setJahreslohn("9990000");
        OptionalPerson1.get().setKontakt("abc@gmail.com");
        OptionalPerson1.get().setSkills(new String[]{"C"});
        OptionalPerson1.get().setId(3L);


        PersonEvent personEvent1 =new PersonEvent();
        personEvent1.setPersonId(2L);

        PersonEvent personEvent2 =new PersonEvent();
        personEvent1.setPersonId(3L);


        personEventList.add(personEvent1);
        personEventList.add(personEvent2);
        when(eventRepository.findAll()).thenReturn(personEventList);


        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

    }
    @Test
    public void getbyId_TEST() throws Exception {
        PersonApi actual=personRestController.getById(2L);
        PersonApi expected= new PersonApi(OptionalPerson1.get());
        assertEquals(actual, expected);
        verify(personRepository,times(1)).findById(any());

    }
    @Test
    public void getEvents(){
        List<PersonEvent> actual= personRestController.getEvents();
        List<PersonEvent> expected=personEventList;

        assertEquals(actual, expected);
        verify(eventRepository,times(1)).deleteAll();
        verify(eventRepository,times(1)).findAll();


    }
    @Test
    public void getAllTest(){
        when(personRepository.findAll()).thenReturn(personList);
        PersonApi personApi1=new PersonApi(person1);
        PersonApi personApi2=new PersonApi(person2);
        List<PersonApi> expected= new ArrayList<>();
        expected.add(personApi1);
        expected.add(personApi2);
        List<PersonApi> actual = personRestController.getAll();
        assertEquals(actual, expected);
        verify(personRepository,times(1)).findAll();

    }
}