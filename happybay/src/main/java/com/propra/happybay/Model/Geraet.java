package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    int kosten;
    int kaution;
    String abholort;
    Date mietezeitpunktStart;
    Date mietezeitpunktEnd;
    String encode;
    int likes = 0;
    boolean forsale;
    @OneToMany(cascade = CascadeType.ALL)
    List<Person> likedPerson = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    List<RentEvent> verfuegbareEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    List<RentEvent> rentEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    List<Bild> bilder = new ArrayList<>();
}

