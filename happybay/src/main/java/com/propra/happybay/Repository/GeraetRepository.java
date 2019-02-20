package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeraetRepository extends CrudRepository<Geraet,Long> {
    List<Geraet> findAll();
    List<Geraet> findAllByBesitzer(String username);
    List<Geraet> findAllByTitelLike(String key);
    void deleteById(Long id);
    List<Geraet> findAllByTitelLike(String key);
    List<Geraet> findAllByMieter(String mieterName);
}
