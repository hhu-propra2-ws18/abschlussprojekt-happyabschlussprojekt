package com.propra.happybay.Controller;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    private Person person = new Person();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    public PasswordEncoder encoder;

    private MockMvc mvc;

    @Autowired
    PersonRepository personRepository;

    @Before
    public void setup() throws IOException {
        person.setUsername("test");
        person.setId(1L);

        personRepository.save(person);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "test", roles = "ADMIN")
    @Test
    public void proflie() throws Exception {
        mvc.perform(get("/admin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "ADMIN")
    @Test
    public void conflicts() throws Exception {
        mvc.perform(get("/admin/conflicts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}