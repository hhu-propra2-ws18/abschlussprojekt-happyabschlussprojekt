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
}

