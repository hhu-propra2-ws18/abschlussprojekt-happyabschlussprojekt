package com.propra.happybay.Repository;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRepositoryTest {

    @Autowired
    PersonRepository repo;

    @Test
    public void testPersonFindAll(){
        Bild b1 = new Bild();

        Person p1 = new Person("tony","stark","123","uni","anton",
                "1234","1234","p","R",b1,"123");
        repo.save(p1);

        List<Person> persons = repo.findAll();

        Assertions.assertThat(persons.get(0).getId()).isEqualTo(p1.getId());
    }


    @Test
    public void testPersonFindByUsername(){
        Bild b1 = new Bild();

        Person p1 = new Person("tony","stark","123","uni","anton",
                "1234","1234","p","R",b1,"123");
        repo.save(p1);

        Optional<Person> persons = repo.findByUsername("anton");

        Assertions.assertThat(persons.get().getId()).isEqualTo(p1.getId());

    }
}