package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Geraet {

    @Id
    @GeneratedValue
    Long id;
    String besitzer;
    @Column(length = 1000)
    String beschreibung;
    String titel;
    boolean verfuegbar;
    String mieter;

    int zeitraum;

    int kosten;
    int kaution;
    String abholort;
    Date mietezeitpunktStart;
    Date mietezeitpunktEnd;

    Date mietezeitpunkt;

    LocalDate endzeitpunkt;
    String encode;
    ReturnStatus returnStatus = ReturnStatus.DEFAULT;
    String grundReturn = "Das Gerät ist beschädigt";
    int likes = 0;

    @OneToMany(cascade = CascadeType.ALL)
    List<RentEvent> verfuegbareEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    List<RentEvent> rentEvents;
    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder = new ArrayList<>();
}

