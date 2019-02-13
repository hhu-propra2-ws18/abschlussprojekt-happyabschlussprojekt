package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Data
@Entity
public class Geraet {
    @Id
    @GeneratedValue
    Long id;
    String titel;
    String beschreibung;
    boolean verfuegbar;
    @OneToOne
    Person besitzer;
    int kosten;
    int kaution;
    String abholOrt;
}

