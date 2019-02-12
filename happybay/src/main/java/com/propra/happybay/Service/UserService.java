package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private PersonRepository personRepository;
   // @Autowired
    //private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveUser(Person person) {
        //person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        personRepository.save(person);
    }
}
