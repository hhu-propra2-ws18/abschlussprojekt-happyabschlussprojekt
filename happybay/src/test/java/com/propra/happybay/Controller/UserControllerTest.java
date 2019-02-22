package com.propra.happybay.Controller;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private Geraet geraet = new Geraet();
    @Autowired
    private WebApplicationContext context;
    @Autowired
    public PasswordEncoder encoder;
//    @Mock
//    Principal principal = new Principal() {
//        @Override
//        public String getName() {
//            return "test";
//        }
//    };

    private MockMvc mvc;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;

    @Before
    public void setup() throws IOException {
        person.setUsername("test");
        person.setId(1L);
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setAdresse("test dusseldorf");
        personRepository.save(person);

        geraet.setTitel("Das ist ein Test");
        geraet.setId(2L);
        geraet.setBesitzer(person.getUsername());
        geraet.setKosten(3);
        geraet.setKaution(10);
        geraetRepository.save(geraet);
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void proflie() throws Exception {
        mvc.perform(get("/user/profile").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void myThings() throws Exception {
        mvc.perform(get("/user/myThings").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void makeNotifications() throws Exception {
        mvc.perform(get("/user/notifications").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void anfragenGet() throws Exception {
        mvc.perform(get("/user/anfragen/{id}",1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @WithMockUser(value = "test", roles = "USER")
    @Test
    public void anfragenPost() throws Exception {
        mvc.perform(post("/user/addGeraet").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}