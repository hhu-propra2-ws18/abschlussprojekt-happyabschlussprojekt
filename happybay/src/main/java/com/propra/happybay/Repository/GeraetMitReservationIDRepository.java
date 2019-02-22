package com.propra.happybay.Repository;


import com.propra.happybay.Model.GeraetMitReservationID;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GeraetMitReservationIDRepository extends CrudRepository<GeraetMitReservationID,Integer> {
    GeraetMitReservationID findByGeraetID(Long geraetId);
    void deleteByReservationID(Long reservationId);
}
