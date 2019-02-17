package com.propra.happybay.Repository;

import com.propra.happybay.Model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person,Long> {
    List<Person> findAll();
    Optional<Person> findByUsername(String username);

}
