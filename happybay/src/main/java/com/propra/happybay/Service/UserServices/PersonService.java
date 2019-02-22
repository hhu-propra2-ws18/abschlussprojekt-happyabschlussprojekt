package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;

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
            if ((verfuegbar.getStart().getTime() < anfrageInterval.getStart().getTime()) && (verfuegbar.getEnd().getTime() > anfrageInterval.getEnd().getTime())) {
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
}
