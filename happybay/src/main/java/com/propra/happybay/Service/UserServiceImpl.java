package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    BCryptPasswordEncoder encoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PersonRepository personRepository;
    @Override
    public void save(Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        person.setRoles(new HashSet<>(roleRepository.findAll()));
        personRepository.save(person);
    }

    @Override
    public Person findByUsername(String username) {
        return personRepository.findByUsername(username);
    }
}
