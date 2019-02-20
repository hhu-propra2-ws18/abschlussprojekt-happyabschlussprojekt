package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.ReturnStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeraetRepository extends CrudRepository<Geraet,Long> {
    List<Geraet> findAll();
    List<Geraet> findAllByBesitzer(String username);
    List<Geraet> findAllByTitelLike(String key);
    void deleteById(Long id);
    List<Geraet> findAllByMieter(String mieterName);

    List<Geraet> findAllByReturnStatus(ReturnStatus status);
}
