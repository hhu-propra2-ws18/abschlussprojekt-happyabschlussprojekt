package com.propra.happybay.Repository;

import com.propra.happybay.Model.RentEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RentEventRepository extends CrudRepository<RentEvent, Long> {
    List<RentEvent> findAllByMieter(String mieter);

}
