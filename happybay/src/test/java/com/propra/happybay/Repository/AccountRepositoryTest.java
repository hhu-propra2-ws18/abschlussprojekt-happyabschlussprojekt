package com.propra.happybay.Repository;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Reservation;
import lombok.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository repo;

    @Test
    public void testAccountFindByAccount() {



        Reservation r1 = new Reservation(1L,30.0);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(r1);

        Account a1 = new Account("a1",30.0,reservations);
        repo.save(a1);

        Optional<Account> accounts = repo.findByAccount("a1");

        Assertions.assertThat(accounts.isPresent()).isEqualTo("true");
        Assertions.assertThat(accounts.get().getAccount()).isEqualTo("a1");
    }


}