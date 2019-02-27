package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
public class RentEvent {
    @Id
    @GeneratedValue
    private Long Id;
    private Long reservationId;
    private String grundForReturn = "";

    @OneToOne(cascade = CascadeType.ALL)
    private Geraet geraet;
    @Embedded
    private TimeInterval timeInterval;
    @OneToOne(cascade = CascadeType.ALL)
    private Person mieter;
    ReturnStatus returnStatus = ReturnStatus.OK;

}
