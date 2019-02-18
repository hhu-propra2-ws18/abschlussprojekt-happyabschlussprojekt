package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class Geraet {

    @Id
    @GeneratedValue
    Long id;
    String titel;
    @Column(length = 1000)
    String beschreibung;
    boolean verfuegbar;
    String besitzer;

    String mieter;
    int zeitraum;
    int kosten;
    double kaution;
    String abholort;
    Date mietezeitpunkt;
    String encode;
    String returnStatus="default";
    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder;
}

