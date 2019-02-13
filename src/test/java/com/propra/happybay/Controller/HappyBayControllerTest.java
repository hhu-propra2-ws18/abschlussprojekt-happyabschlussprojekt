package com.propra.happybay.Controller;

import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class HappyBayControllerTest {
    private MockMvc mockMvc;
    @Mock
    PersonRepository personRepository;
    @Mock
    GeraetRepository geraetRepository;
    @Before
    public void setUp() throws Exception {

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        this.mockMvc = MockMvcBuilders.standaloneSetup(new HappyBayController(personRepository,geraetRepository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andDo(print());

    }

    @Test
    public void addUser() throws Exception {
        mockMvc.perform(get("/addUser"))
                .andExpect(status().isOk())
                .andExpect(view().name("addUser"))
                .andDo(print());
    }

//    @Test
//    public void confirmationAdd() throws Exception {
//        mockMvc.perform(get("/confirmationAdd"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("confirmationAdd"))
//                .andDo(print());
//    }

    @Test
    public void administrator() throws Exception {

    }

    @Test
    public void user() {
    }
}