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

    @OneToOne(cascade = CascadeType.ALL)
    private Geraet geraet;

    @OneToOne(cascade = CascadeType.ALL)
    private RentEvent rentEvent;

    @OneToOne(cascade = CascadeType.ALL)
    private Person besitzer;

    @OneToOne(cascade = CascadeType.ALL)
    private Person anfragePerson;

    public String getAnfragePersonUsername() {
        return anfragePerson.getUsername();
    }

    public String getBesitzerUsername() {
        return besitzer.getUsername();
    }
}

