//package com.propra.happybay.Controller;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.junit.Assert.assertThat;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminControllerTest {
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    @WithMockUser(username = "testAdmin", password = "testAdmin", roles = "ADMIN")
//    public void home() throws Exception {
//        String body = this.restTemplate.getForObject("/", String.class);
//        assertThat(body).isEqualTo("Hello World!");
//    }
//
//}