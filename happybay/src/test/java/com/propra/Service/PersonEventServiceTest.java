package propra2.person.Service;

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
import propra2.person.Model.Person;
import propra2.person.Model.PersonEvent;
import propra2.person.Repository.EventRepository;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonEventServiceTest {
    @Mock
    EventRepository eventRepository;
    @InjectMocks
    PersonEventService personEventService;


    @Test
    public void createEvent_TEST(){
        Person person1=new Person();
        person1.setId(1L);
        PersonEvent personEvent_create=new PersonEvent();
        personEvent_create.setEvent("create");
        personEvent_create.setPersonId(1L);
        personEventService.createEvent(person1);
        verify(eventRepository,times(1)).save(personEvent_create);
    }
    @Test
    public void editEvent_TEST(){
        Person person2=new Person();
        person2.setId(2L);
        PersonEvent personEvent_edit=new PersonEvent();
        personEvent_edit.setEvent("edit");
        personEvent_edit.setPersonId(2L);
        personEventService.editEvent(2L);
        verify(eventRepository,times(1)).save(personEvent_edit);
    }
    @Test
    public void deleteEvent_TEST(){
        Person person3=new Person();
        person3.setId(3L);
        PersonEvent personEvent_delete=new PersonEvent();
        personEvent_delete.setEvent("delete");
        personEvent_delete.setPersonId(3L);
        personEventService.deleteEvent(3L);
        verify(eventRepository,times(1)).save(personEvent_delete);
    }
}