package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.util.List;

@Service
public class MailService {
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private PersonRepository personRepository;


    //@Scheduled(fixedRate=86400000)
    //public void sendScheduledMail() throws Exception{
    //
    //    List<Geraet> geraets = geraetRepository.findAll();
    //    for(int i=0;i<geraets.size();i++){
    //        if (geraets.get(i).isVerfuegbar() == false && geraets.get(i).getZeitraum() <= 3 && geraets.get(i).getZeitraum() > 0
    //                && geraets.get(i).getReturnStatus() == ReturnStatus.DEFAULT) {
    //            Person person = personRepository.findByUsername(geraets.get(i).getBesitzer()).get();
    //            MimeMessage message1 = sender.createMimeMessage();
    //            MimeMessageHelper helper1 = new MimeMessageHelper(message1);
    //            helper1.setTo(person.getKontakt());
    //            helper1.setSubject("Rückkehrzeit");
    //            helper1.setText("Ihre Vermietung(" + geraets.get(i).getTitel()+ ") ist fast abgelaufen." );
    //            sender.send(message1);
    //            geraets.get(i).setZeitraum(geraets.get(i).getZeitraum()-1);
    //            geraetRepository.save(geraets.get(i));
    //        } else if (geraets.get(i).isVerfuegbar() == false && geraets.get(i).getZeitraum() == 0
    //                && geraets.get(i).getReturnStatus() == ReturnStatus.DEFAULT) {
    //            Person person = personRepository.findByUsername(geraets.get(i).getBesitzer()).get();
    //            MimeMessage message1 = sender.createMimeMessage();
    //            MimeMessageHelper helper1 = new MimeMessageHelper(message1);
    //            helper1.setTo(person.getKontakt());
    //            helper1.setSubject("Rückkehrzeit");
    //            helper1.setText("Ihre Vermietung(" + geraets.get(i).getTitel() + ") ist abgelaufen.");
    //            sender.send(message1);
    //        } else if (geraets.get(i).isVerfuegbar() == false && geraets.get(i).getZeitraum() > 3) {
    //            geraets.get(i).setZeitraum(geraets.get(i).getZeitraum() - 1);
    //            geraetRepository.save(geraets.get(i));
    //        }
    //    }
    //}

    public void sendAnfragMail(Person person, Geraet geraet, Principal principal) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Du hast eine neue Anfrag über("+ geraet.getTitel() + ") von " + principal.getName());
        helper.setSubject("Anfrag");
        //sender.send(message);
    }

    public void sendReturnMail(Person person,Geraet geraet) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Ihre Geraet (" + geraet.getTitel()+ ") wurde zur Bewerbung zurückgeschickt" );
        helper.setSubject("Bewerbung zurücksenden");
        //sender.send(message);
    }

    public void sendRefuseRequestMail(Person person,Geraet geraet) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Ihre Mietanfrage (" + geraet.getTitel()+ ") wird abgelehnt." );
        helper.setSubject("Antragsergebnis");
        //sender.send(message);
    }

    public void sendAcceptRequestMail(Person person,Geraet geraet) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Ihre Mietanfrage (" + geraet.getTitel()+ ") wird akzeptiert." );
        helper.setSubject("Antragsergebnis");
        //sender.send(message);
    }

    public void sendRefuseReturnMail(Person person,Geraet geraet) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Ihre Rückkehr über(" + geraet.getTitel()+ ") wird abgelehnt." );
        helper.setSubject("Ergebnis zurückgeben");
        //sender.send(message);
    }

    public void sendAcceptReturnMail(Person person,Geraet geraet) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(person.getKontakt());
        helper.setText("Ihre Rückkehr über(" + geraet.getTitel()+ ") ist erfolgreich." );
        helper.setSubject("Ergebnis zurückgeben");
        //sender.send(message);
    }

}
