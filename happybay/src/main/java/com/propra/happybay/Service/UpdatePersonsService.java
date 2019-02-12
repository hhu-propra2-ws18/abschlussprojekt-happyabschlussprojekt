//package com.propra.happybay.Service;
//
//import com.propra.happybay.Model.Person;
//import com.propra.happybay.Repository.PersonRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UpdatePersonsService {
//    @Autowired
//    PersonRepository personRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public void save(Person person) {
//        person.setPassword(passwordEncoder.encode(person.getPassword()));
//        personRepository.save(person);
//    }
//}
