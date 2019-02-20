package com.propra.happybay.Controller;

import com.propra.happybay.Repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Mock
    private PersonRepository personRepository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .standaloneSetup(new UserController(personRepository))
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser("test")
    @Test
    public void profile() throws Exception {
        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void myThings() {
    }

    @Test
    public void rentThings() {
    }

    @Test
    public void makeNotifications() {
    }

    @Test
    public void anfragen() {
    }

    @Test
    public void anfragen1() {
    }

    @Test
    public void addGeraet() {
    }

    @Test
    public void confirmGeraet() {
    }

    @Test
    public void proPay() {
    }

    @Test
    public void propay() {
    }

    @Test
    public void geraet() {
    }

    @Test
    public void besitzerInfo() {
    }

    @Test
    public void geraetEdit() {
    }

    @Test
    public void geraetZurueck() {
    }

    @Test
    public void geraetDelete() {
    }

    @Test
    public void notificationRefuseRequest() {
    }

    @Test
    public void notificationAcceptRequest() {
    }

    @Test
    public void notificationRefuseReturn() {
    }

    @Test
    public void notificationAcceptReturn() {
    }

    @Test
    public void changeImg() {
    }

    @Test
    public void chageProfile() {
    }

    @Test
    public void geraetEdit1() {
    }

    @Test
    public void bezahlen() {
    }

    @Test
    public void about() {
    }

    @Test
    public void like() {
    }
}