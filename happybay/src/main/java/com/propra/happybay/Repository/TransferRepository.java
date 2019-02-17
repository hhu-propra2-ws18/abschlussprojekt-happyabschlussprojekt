package com.propra.happybay.Repository;

import com.propra.happybay.Model.Transfer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransferRepository extends CrudRepository<Transfer,Long> {
    List<Transfer> findAll();
}