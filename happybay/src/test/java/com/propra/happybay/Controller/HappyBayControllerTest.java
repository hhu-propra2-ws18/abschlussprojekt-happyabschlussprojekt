package com.propra.happybay.Controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@WebAppConfiguration
public class HappyBayControllerTest {
    private MockMvc mockMvc;
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void index() {
    }

    @Test
    public void addUser() {
    }

    @Test
    public void addToDatabase() {
    }

    @Test
    public void administrator() {
    }

    @Test
    public void user() {
    }
}