package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Geraet {
    @Id
    Long id;
    String titel;
    String beschreibung;
    boolean verfuegbar;
    Person besitzer;
    int kosten;
    int kaution;
    String abholort;
}

