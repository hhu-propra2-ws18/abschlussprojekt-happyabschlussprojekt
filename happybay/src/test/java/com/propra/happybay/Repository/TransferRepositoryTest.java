package com.propra.happybay.Repository;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.Transfer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class TransferRepositoryTest {

    @Autowired
    TransferRepository repo;

    @Test
    public void testTransferFindAll(){

        Transfer t1 = new Transfer("z","o",12);
        repo.save(t1);
        List<Transfer> transfers = repo.findAll();

        Assertions.assertThat(transfers.get(0).getId()).isEqualTo(t1.getId());
    }
}