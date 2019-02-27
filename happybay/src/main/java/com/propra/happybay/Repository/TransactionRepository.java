package com.propra.happybay.Repository;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByReceiverOrGiver(Person person1, Person person2);
}
