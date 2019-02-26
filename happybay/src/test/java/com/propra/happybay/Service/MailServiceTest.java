package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import com.propra.happybay.Service.UserServices.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MailServiceTest {


    @Mock
    private GeraetRepository geraetRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private RentEventRepository rentEventRepository;
    @Mock
    private JavaMailSender sender;

    @InjectMocks
    MailService mailService;


    @Test
    public void send_Scheduled_Mail() throws Exception {
        List<RentEvent> rentEventList = new ArrayList<>();
        RentEvent rentEvent1 = new RentEvent();
        rentEvent1.setGeraetId(1L);
        rentEvent1.setReturnStatus(ReturnStatus.DEADLINE_CLOSE);
        rentEventList.add(rentEvent1);

        RentEvent rentEvent2 = new RentEvent();
        rentEvent2.setGeraetId(2L);
        rentEvent2.setReturnStatus(ReturnStatus.DEADLINE_OVER);
        rentEventList.add(rentEvent2);
        Mockito.when(rentEventRepository.findAll()).thenReturn(rentEventList);

        Person fakePerson = new Person();
        fakePerson.setKontakt("a@b.c");
        Mockito.when(personRepository.findByUsername(any())).thenReturn(java.util.Optional.of(fakePerson));
        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(sender.createMimeMessage()).thenReturn(msg);

        Geraet fakeGeraet = new Geraet();
        fakeGeraet.setTitel("fake Geraet");
        Mockito.when(geraetRepository.findById(anyLong())).thenReturn(java.util.Optional.ofNullable(fakeGeraet));

        mailService.sendScheduledMail();
        verify(sender,times(2)).createMimeMessage();
        verify(sender,times(2)).send((MimeMessage) any());
    }

    @Test
    public void send_mail() throws Exception {
        Principal principal = new Principal() {
            @Override
            public String getName() {
                return null;
            }
        };
        Geraet fakeGeraet = new Geraet();
        fakeGeraet.setTitel("fake");
        Person fakePerson = new Person();
        fakePerson.setKontakt("kerrie.zhang@qq.com");
        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(sender.createMimeMessage()).thenReturn(msg);
        mailService.sendAnfragMail(fakePerson,fakeGeraet,principal);
        mailService.sendReturnMail(fakePerson,fakeGeraet);
        mailService.sendAcceptReturnMail(fakePerson,fakeGeraet);
        mailService.sendRefuseReturnMail(fakePerson,fakeGeraet);
        mailService.sendAcceptRequestMail(fakePerson,fakeGeraet);
        mailService.sendRefuseRequestMail(fakePerson,fakeGeraet);
        verify(sender,times(6)).createMimeMessage();
        verify(sender,times(6)).send((MimeMessage) any());

    }

}