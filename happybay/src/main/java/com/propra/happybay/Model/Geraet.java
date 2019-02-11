package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Geraet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id;
    String titel;
    String beschreibung;
    boolean verfuegbar;
    @OneToOne(cascade = CascadeType.ALL)
    Person besitzer;
    int kosten;
    int kaution;
    String abholort;
}

