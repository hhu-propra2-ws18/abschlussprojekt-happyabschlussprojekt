package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.UserServices.PersonService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;


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


}
