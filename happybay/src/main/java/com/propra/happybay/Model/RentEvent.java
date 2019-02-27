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
    Long Id;
    int reservationId;
    Long geraetId;
    String grundForReturn = "";
    @Embedded
    TimeInterval timeInterval;
    String mieter; //username
    ReturnStatus returnStatus = ReturnStatus.OK;
}
