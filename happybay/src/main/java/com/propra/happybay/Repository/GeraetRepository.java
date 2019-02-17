package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeraetRepository extends CrudRepository<Geraet,Long> {
    List<Geraet> findAll();
    List<Geraet> findAllByBesitzer(String username);
    void deleteById(Long id);

    List<Geraet> findAllByMieter(String mieterName);
}
