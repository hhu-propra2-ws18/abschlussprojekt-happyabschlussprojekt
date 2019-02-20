package com.propra.happybay;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class HappybayApplication {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    public PasswordEncoder encoder;
    public static void main(String[] args) {
        SpringApplication.run(HappybayApplication.class, args);
    }

    @Bean
    InitializingBean initializeAdmin() {
        return () -> {
            Person admin = new Person();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            if(!personRepository.findByUsername("admin").isPresent()) {
                personRepository.save(admin);
            }
        };
    }
}

