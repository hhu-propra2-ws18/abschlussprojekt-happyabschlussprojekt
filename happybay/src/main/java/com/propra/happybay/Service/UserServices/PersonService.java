package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;

    public Person getByUsername(String username) {
        return personRepository.findByUsername(username).get();
    }


    public void increaseAktionPunkte(String signedInPersonUsername) {
        Person person = getByUsername(signedInPersonUsername);
        int aktionPunkte = person.getAktionPunkte();
        person.setAktionPunkte(aktionPunkte + 10);
        personRepository.save(person);
    }
}
