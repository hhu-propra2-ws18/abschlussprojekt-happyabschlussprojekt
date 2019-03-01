package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.ReturnStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeraetRepository extends CrudRepository<Geraet, Long> {
    List<Geraet> findAll();

    List<Geraet> findAllByBesitzer(Person besitzer);

    List<Geraet> findAllByTitelLike(String key);

    List<Geraet> findAllByTitelLikeOrderByLikesDesc(String key);

    List<Geraet> findAllByTitelLikeOrderByKostenAsc(String key);

    List<Geraet> findAllByTitelLikeOrderByKostenDesc(String key);

    List<Geraet> findAllByTitelLikeOrderByLikesAsc(String key);

    void deleteById(Long id);
}
