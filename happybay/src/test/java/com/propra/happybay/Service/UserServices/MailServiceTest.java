package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
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

import javax.mail.internet.MimeMessage;
import java.security.Principal;
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
    private RentEventRepository rentEventRepository;
    @Mock
    private JavaMailSender sender;

    @InjectMocks
    MailService mailService;


    @Test
    public void send_Scheduled_Mail() throws Exception {
        List<RentEvent> rentEventList = new ArrayList<>();

        Person fakePerson = new Person();
        fakePerson.setKontakt("a@b.c");

        Geraet fakeGeraet = new Geraet();
        fakeGeraet.setTitel("fake Geraet");

        RentEvent rentEvent1 = new RentEvent();
        rentEvent1.setGeraet(fakeGeraet);
        rentEvent1.setMieter(fakePerson);
        rentEvent1.setReturnStatus(ReturnStatus.DEADLINE_CLOSE);
        rentEventList.add(rentEvent1);

        RentEvent rentEvent2 = new RentEvent();
        rentEvent2.setGeraet(fakeGeraet);
        rentEvent2.setMieter(fakePerson);
        rentEvent2.setReturnStatus(ReturnStatus.DEADLINE_OVER);
        rentEventList.add(rentEvent2);
        Mockito.when(rentEventRepository.findAll()).thenReturn(rentEventList);


        MimeMessage msg = new JavaMailSenderImpl().createMimeMessage();
        Mockito.when(sender.createMimeMessage()).thenReturn(msg);

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