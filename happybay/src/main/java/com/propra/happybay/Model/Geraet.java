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
    @Column(columnDefinition = "NVARCHAR(MAX)")
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

    public Geraet(String titel, String beschreibung, boolean verfuegbar, String besitzer, String mieter, int zeitraum,
                  int kosten, double kaution, String abholort, Date mietezeitpunkt, String encode, String returnStatus,
                  List<Bild> bilder) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.verfuegbar = verfuegbar;
        this.besitzer = besitzer;
        this.mieter = mieter;
        this.zeitraum = zeitraum;
        this.kosten = kosten;
        this.kaution = kaution;
        this.abholort = abholort;
        this.mietezeitpunkt = mietezeitpunkt;
        this.encode = encode;
        this.returnStatus = returnStatus;
        this.bilder = bilder;
    }
}

