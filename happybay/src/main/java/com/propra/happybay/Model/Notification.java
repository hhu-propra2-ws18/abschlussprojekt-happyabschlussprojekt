package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue
    Long id;
    Long geraetId;
    String message;
    String anfragePerson;
    Date mietezeitPunkt;
    int zeitraum;


    public Notification(Long geraetId, String message, String anfragePerson, Date mietezeitPunkt, int zeitraum) {
        this.geraetId = geraetId;
        this.message = message;
        this.anfragePerson = anfragePerson;
        this.mietezeitPunkt = mietezeitPunkt;
        this.zeitraum = zeitraum;
    }

}

