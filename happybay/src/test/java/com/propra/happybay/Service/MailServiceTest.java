//package com.propra.happybay.Service;
//
//import com.propra.happybay.Model.Geraet;
//import com.propra.happybay.Model.Person;
//import com.propra.happybay.Repository.GeraetRepository;
//import com.propra.happybay.Repository.PersonRepository;
//import com.propra.happybay.Service.UserServices.MailService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.TestContext;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.web.context.WebApplicationContext;
//
//import javax.mail.internet.MimeMessage;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
//@WebAppConfiguration
//public class MailServiceTest {
//
//
//    @Mock
//    private GeraetRepository geraetRepository;
//    @Mock
//    private PersonRepository personRepository;
//    @Mock
//    private JavaMailSender sender;
//
//    @InjectMocks
//    MailService mailService;
//
//
//    @Test
//    public void send_Scheduled_Mail_in3day() throws Exception {
//        Geraet fakeGeraet = new Geraet();
//        List<Geraet> fakeGeraets = new ArrayList<>();
//        fakeGeraet.setZeitraum(2);
//        fakeGeraet.setVerfuegbar(false);
//        fakeGeraets.add(fakeGeraet);
//        Mockito.when(geraetRepository.findAll()).thenReturn(fakeGeraets);
//
//        Person fakePerson = new Person();
//        fakePerson.setKontakt("a@b.c");
//        Mockito.when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.of(fakePerson));
//        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
//        Mockito.when(sender.createMimeMessage()).thenReturn(msg);
//
//        mailService.sendScheduledMail();
//        verify(geraetRepository,times(1)).save(any());
//        verify(sender,times(1)).createMimeMessage();
//        verify(sender,times(1)).send((MimeMessage) any());
//    }
//
//    @Test
//    public void send_Scheduled_Mail_overTime() throws Exception {
//        Geraet fakeGeraet = new Geraet();
//        List<Geraet> fakeGeraets = new ArrayList<>();
//        fakeGeraet.setZeitraum(0);
//        fakeGeraet.setVerfuegbar(false);
//        fakeGeraets.add(fakeGeraet);
//        Mockito.when(geraetRepository.findAll()).thenReturn(fakeGeraets);
//
//        Person fakePerson = new Person();
//        fakePerson.setKontakt("a@b.c");
//        Mockito.when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.of(fakePerson));
//        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
//        Mockito.when(sender.createMimeMessage()).thenReturn(msg);
//
//        mailService.sendScheduledMail();
//        verify(sender,times(1)).createMimeMessage();
//        verify(sender,times(1)).send((MimeMessage) any());
//    }
//
//    @Test
//    public void send_Scheduled_Mail_out3day() throws Exception {
//        Geraet fakeGeraet = new Geraet();
//        List<Geraet> fakeGeraets = new ArrayList<>();
//        fakeGeraet.setZeitraum(4);
//        fakeGeraet.setVerfuegbar(false);
//        fakeGeraets.add(fakeGeraet);
//        Mockito.when(geraetRepository.findAll()).thenReturn(fakeGeraets);
//
//        mailService.sendScheduledMail();
//        verify(geraetRepository,times(1)).save(any());
//    }
//
//    @Test
//    public void send_mail() throws Exception {
//        Principal principal = new Principal() {
//            @Override
//            public String getName() {
//                return null;
//            }
//        };
//        Geraet fakeGeraet = new Geraet();
//        fakeGeraet.setTitel("fake");
//        Person fakePerson = new Person();
//        fakePerson.setKontakt("kerrie.zhang@qq.com");
//        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
//        Mockito.when(sender.createMimeMessage()).thenReturn(msg);
//        mailService.sendAnfragMail(fakePerson,fakeGeraet,principal);
//        mailService.sendReturnMail(fakePerson,fakeGeraet);
//        mailService.sendAcceptReturnMail(fakePerson,fakeGeraet);
//        mailService.sendRefuseReturnMail(fakePerson,fakeGeraet);
//        mailService.sendAcceptRequestMail(fakePerson,fakeGeraet);
//        mailService.sendRefuseRequestMail(fakePerson,fakeGeraet);
//        verify(sender,times(6)).createMimeMessage();
//        verify(sender,times(6)).send((MimeMessage) any());
//
//    }
//
//}