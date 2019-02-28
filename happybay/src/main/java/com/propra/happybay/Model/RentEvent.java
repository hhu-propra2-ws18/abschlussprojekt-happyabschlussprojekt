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
    private int reservationId;
    private String grundForReturn = "";

    @ManyToOne
    private Geraet geraet;
    @Embedded
    private TimeInterval timeInterval;
    @ManyToOne
    private Person mieter;
    ReturnStatus returnStatus = ReturnStatus.OK;

    public double calculatePrice() {
        return this.timeInterval.getDuration() * this.geraet.getKosten();
    }
}
