package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    int zeitraum;
    int kosten;
    double kaution;
    String abholort;
    LocalDate endzeitpunkt;
    String encode;
    String returnStatus="default";
    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder=null;
    int[] genre;
}

