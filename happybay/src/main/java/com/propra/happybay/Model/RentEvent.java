package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RentEvent {
    @Id
    @GeneratedValue
    Long Id;

    @Embedded
    TimeInterval timeInterval;

    String mieter; //username

    ReturnStatus returnStatus = ReturnStatus.DEFAULT;

}
