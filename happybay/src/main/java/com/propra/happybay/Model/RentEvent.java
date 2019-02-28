package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;

@Data
@ToString(exclude = "geraet")
@Entity
public class RentEvent {
    @Id
    @GeneratedValue
    private Long Id;
    private int reservationId;
    private String grundForReturn = "";

    @ManyToOne
    private Geraet geraet;
    @Embedded
    private TimeInterval timeInterval;
    @ManyToOne
    private Person mieter;
    ReturnStatus returnStatus = ReturnStatus.OK;
}
