package com.propra.happybay.Repository;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Bild;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BildRepository extends CrudRepository<Bild,Long> {
}
