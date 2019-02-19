package com.propra.happybay.Controller;

import com.propra.happybay.Repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HappyBayControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    PersonRepository personRepository;

    @Autowired
    HappyBayController happyBayController;

    @Test

    public void callIndexPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callAddUserPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/addUser"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callAdminPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/admin"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    public void callUserPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callPersonInfoPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/personInfo"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    public void callProfilePage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/profile"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    public void callMyThingsPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/myThings"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callRentThingsPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/rentThings"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callMyRemindPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("myRemind"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callAddGeraetPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/addGeraet"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callLoginPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callPropayPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/proPay"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callGeraetPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/geraet/{id}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callEditGeraetPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/geraet/edit/{id}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    public void callChangeProfilePage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/PersonInfo/Profile/ChangeProfile"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    public void callPeoplePayPage() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/bezahlen/{id}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }



}