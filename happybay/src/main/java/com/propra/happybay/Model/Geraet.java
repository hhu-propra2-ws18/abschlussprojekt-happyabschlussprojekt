package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
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
    String besitzer;
    @Column(length = 1000)
    String beschreibung;
    String titel;
    boolean verfuegbar;
    String mieter;
    int zeitraum;
    int kosten;
    double kaution;
    String abholort;
    Date mietezeitpunkt;
    String encode;
    ReturnStatus returnStatus = ReturnStatus.DEFAULT;
    int likes = 0;
    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder;
}

