package com.propra.happybay.Repository;

import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.ReturnStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RentEventRepository extends CrudRepository<RentEvent, Long> {
    List<RentEvent> findAll();
    List<RentEvent> findAllByMieter(String mieter);
    List<RentEvent> findAllByReturnStatus(ReturnStatus returnStatus);
    RentEvent findByReservationId(int reservationId);
    void delete(RentEvent rentEvent);
    List<RentEvent> findAllByMieterAndReturnStatus(String mieter, ReturnStatus returnStatus);

}
