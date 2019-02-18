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
    String beschreibung;
    boolean verfuegbar;
    String besitzer;
    String mieter;
    int kosten;
    int kaution;
    String abholort;
    Date oeffdatum;
    String encode;

    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder;

    public Geraet(String titel, String beschreibung, boolean verfuegbar, String besitzer, String mieter, int kosten, int kaution, String abholort, Date oeffdatum, String encode,List<Bild> bilder) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.verfuegbar = verfuegbar;
        this.besitzer = besitzer;
        this.mieter = mieter;
        this.kosten = kosten;
        this.kaution = kaution;
        this.abholort = abholort;
        this.oeffdatum = oeffdatum;
        this.encode = encode;
        this.bilder = bilder;
    }
}

