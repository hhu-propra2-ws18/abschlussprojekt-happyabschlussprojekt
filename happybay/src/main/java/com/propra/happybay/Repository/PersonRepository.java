package com.propra.happybay.Repository;

import com.propra.happybay.Model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<Person,Long> {
    List<Person> findAll();
}
