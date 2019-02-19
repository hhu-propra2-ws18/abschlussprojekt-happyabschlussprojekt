package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender sender;

    @Scheduled(initialDelay=5000, fixedRate=5000)
    public void sendScheduledMail(Person person, Geraet geraet) throws Exception{
        //  while(){ }
        MimeMessage message1 = sender.createMimeMessage();
        //LocalDate localDate = notification.getMietezeitPunkt().toLocalDate().plusDays(notification.getZeitraum()-3);
        //message1.setSentDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        //message1.setSentDate(new Date().setTime((notification.getMietezeitPunkt().getTime()+(long)(notification.getZeitraum()-3)*24*60*60*1000));
        //message1.saveChanges();

        MimeMessageHelper helper1 = new MimeMessageHelper(message1);
        helper1.setTo(person.getKontakt());
        helper1.setSubject("Rückkehrzeit");
        helper1.setText("Es sind noch 3 Tage für Ihre Vermietung(" + geraet.getTitel()+ ") übrig, bitte senden Sie sie rechtzeitig zurück." );
        //helper1.setSentDate(new Date(notification.getMietezeitPunkt().getTime()+(long)(notification.getZeitraum()-3)*24*60*60*1000));
        sender.send(message1);
    }
}
