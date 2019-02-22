package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class GeraetMitReservationID {
    @Id
    private Long reservationID;
    private Long geraetID;

}
