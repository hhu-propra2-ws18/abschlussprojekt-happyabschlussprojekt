package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.List;

@Data
@Service
public class MailService {

    @Autowired
    private JavaMailSender sender;
    @Autowired
    private GeraetRepository geraetRepository;
    @Autowired
    private PersonRepository personRepository;


    @Scheduled(fixedRate=86400000)
    public void sendScheduledMail() throws Exception{

        List<Geraet> geraets = geraetRepository.findAll();
        for(int i=0;i<geraets.size();i++){
            if(geraets.get(i).isVerfuegbar()==false && geraets.get(i).getZeitraum()<=3 && geraets.get(i).getZeitraum()>0){
                Person person = personRepository.findByUsername(geraets.get(i).getBesitzer()).get();
                MimeMessage message1 = sender.createMimeMessage();
                MimeMessageHelper helper1 = new MimeMessageHelper(message1);
                helper1.setTo(person.getKontakt());
                helper1.setSubject("Rückkehrzeit");
                helper1.setText("Ihre Vermietung(" + geraets.get(i).getTitel()+ ") ist fast abgelaufen." );
                sender.send(message1);
                geraets.get(i).setZeitraum(geraets.get(i).getZeitraum()-1);
                geraetRepository.save(geraets.get(i));
            }
            else if(geraets.get(i).isVerfuegbar()==false && geraets.get(i).getZeitraum()==0){
                Person person = personRepository.findByUsername(geraets.get(i).getBesitzer()).get();
                MimeMessage message1 = sender.createMimeMessage();
                MimeMessageHelper helper1 = new MimeMessageHelper(message1);
                helper1.setTo(person.getKontakt());
                helper1.setSubject("Rückkehrzeit");
                helper1.setText("Ihre Vermietung(" + geraets.get(i).getTitel()+ ") ist fast abgelaufen." );
                sender.send(message1);
            }
        }
    }
}
