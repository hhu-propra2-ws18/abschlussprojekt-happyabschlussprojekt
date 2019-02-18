package com.propra.happybay.Repository;

import com.propra.happybay.Model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account,Long> {
    Optional<Account> findByAccount(String username);
    List<Account> findAll();
}
