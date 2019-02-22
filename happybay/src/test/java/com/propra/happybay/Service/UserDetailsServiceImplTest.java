package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import reactor.test.StepVerifier;

import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class UserDetailsServiceImplTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    private GeraetRepository geraetRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void load_user_by_username(){
        UserDetails fakeUserDetails = User.builder()
                .username("fakeName")
                .password("fakePassword")
                .authorities("FAKE_ROLLER")
                .build();

        Person fakePerson = new Person();
        fakePerson.setUsername("fakeName");
        fakePerson.setPassword("fakePassword");
        fakePerson.setRole("FAKE_ROLLER");
        Mockito.when(personRepository.findByUsername("fakeName")).thenReturn(java.util.Optional.of(fakePerson));
        Assertions.assertThat(userDetailsService.loadUserByUsername("fakeName")).isEqualTo(fakeUserDetails);
    }

//    public void load_fail_user_by_username(){
//        Mockito.when(personRepository.findByUsername("fakeName")).thenReturn(java.util.Optional.of(null));
//        Assertions.assertThat(userDetailsService.loadUserByUsername("fakeName")).isEqualTo(new UsernameNotFoundException("Invalid username"));
//    }
}