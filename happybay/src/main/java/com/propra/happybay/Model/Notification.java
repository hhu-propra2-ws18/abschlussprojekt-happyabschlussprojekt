package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue
    private Long id;
    private String type;
    private String message;
    private Date mietezeitpunktStart;
    private Date mietezeitpunktEnd;
    private String encode;

    @ManyToOne
    private Geraet geraet;

    @ManyToOne
    private RentEvent rentEvent;

    @ManyToOne
    private Person besitzer;

    @ManyToOne
    private Person anfragePerson;
}

