package com.propra.happybay.Controller;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HappyBayRestController {
    @Autowired
    PersonRepository personRepository;
    @GetMapping("/api/all")
    public List<Person> getAll(){
        return personRepository.findAll();
    }
}
