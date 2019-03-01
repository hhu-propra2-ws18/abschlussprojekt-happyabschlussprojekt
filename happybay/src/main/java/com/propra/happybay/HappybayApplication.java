package com.propra.happybay;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@SpringBootApplication
@EnableScheduling
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
            if (!personRepository.findByUsername("admin").isPresent()) {
                personRepository.save(admin);
            }
        };
    }

    @Bean
    InitializingBean initializeTest() {
        return () -> {
            Person admin = new Person();
            admin.setUsername("test");
            admin.setPassword(encoder.encode("test"));
            admin.setRole("ROLE_TEST");
        };
    }

    @Bean
    public JavaMailSender getSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername("happybayteam@gmail.com");
        mailSender.setPassword("happybay123");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }

}
