package com.propra.happybay.Controller;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {
    private MultipartFile file = new MultipartFile() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOriginalFilename() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return new byte[0];
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };
    private Person person = new Person();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    public PasswordEncoder encoder;

    private MockMvc mvc;

    @Autowired
    PersonRepository personRepository;

    @Before
    public void setup() {
        person.setUsername("test");
        person.setPassword(encoder.encode("test"));
        person.setId(1L);
//        Bild bild = new Bild();
//        bild.setBild(file.getBytes());
//        person.setFoto(bild);
        personRepository.save(person);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
    @WithMockUser(value = "test")
    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
//        person.setUsername("test");
//        person.setPassword(encoder.encode("test"));
//        person.setId(1L);
////        Bild bild = new Bild();
////        bild.setBild(file.getBytes());
////        person.setFoto(bild);
//        personRepository.save(person);
        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}