package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.Service.ProPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    RentEventRepository rentEventRepository;
    @Autowired
    public PasswordEncoder encoder;
    @Autowired
    private ProPayService proPayService;

    public Person getByUsername(String username) {
        return personRepository.findByUsername(username).get();
    }

    public void increaseAktionPunkte(String signedInPersonUsername) {
        Person person = getByUsername(signedInPersonUsername);
        int aktionPunkte = person.getAktionPunkte();
        person.setAktionPunkte(aktionPunkte + 10);
        personRepository.save(person);
    }

    public int positionOfFreeBlock(Geraet geraet, RentEvent anfrageRentEvent) {
        TimeInterval anfrageInterval = anfrageRentEvent.getTimeInterval();
        for (int i = 0; i < geraet.getVerfuegbareEvents().size(); i++) {
            TimeInterval verfuegbar = geraet.getVerfuegbareEvents().get(i).getTimeInterval();
            if ((verfuegbar.getStart().getTime() <= anfrageInterval.getStart().getTime()) && (verfuegbar.getEnd().getTime() >= anfrageInterval.getEnd().getTime())) {
                return i;
            }
        }
        return -1;
    }

    public void intervalZerlegen(Geraet geraet, int index, RentEvent rentEvent) {
        TimeInterval timeInterval1 = new TimeInterval(geraet.getVerfuegbareEvents().get(index).getTimeInterval().getStart(),
                rentEvent.getTimeInterval().getStart());
        TimeInterval timeInterval2 = new TimeInterval(rentEvent.getTimeInterval().getEnd(),
                geraet.getVerfuegbareEvents().get(index).getTimeInterval().getEnd());
        RentEvent rentEvent1 = new RentEvent();
        rentEvent1.setTimeInterval(timeInterval1);

        RentEvent rentEvent2 = new RentEvent();
        rentEvent2.setTimeInterval(timeInterval2);
        geraet.getVerfuegbareEvents().add(rentEvent1);
        geraet.getVerfuegbareEvents().add(rentEvent2);
        geraet.getVerfuegbareEvents().remove(index);
        geraetRepository.save(geraet);
    }

    public void makeComment(Geraet geraet, Person person, String grund) {
        Comment comment = new Comment();
        comment.setDate(LocalDate.now());
        comment.setGeraetTitel(geraet.getTitel());
        comment.setMessage(grund);
        comment.setSenderFrom(personRepository.findByUsername(geraet.getBesitzer()).get().getUsername());
        comment.setPersonId(personRepository.findByUsername(geraet.getBesitzer()).get().getId());
        person.getComments().add(comment);
        personRepository.save(person);
    }

    public void makeAndSaveNewPerson(MultipartFile file, Person person) throws IOException {
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        person.setFoto(bild);
        person.setRole("ROLE_USER");
        person.setPassword(encoder.encode(person.getPassword()));
        personRepository.save(person);
        proPayService.saveAccount(person.getUsername());
    }
}
