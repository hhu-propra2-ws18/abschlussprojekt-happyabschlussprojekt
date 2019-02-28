package com.propra.happybay.Model;

import com.propra.happybay.ReturnStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data @ToString
@Entity
public class Geraet {

    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 1000)
    private String beschreibung;
    private String titel;
    private int kosten;
    private int kaution;
    private String abholort;
    private Date mietezeitpunktStart;
    private Date mietezeitpunktEnd;
    private String encode;
    private int likes = 0;
    private boolean forsale;

    @ManyToOne
    private Person besitzer;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Person> likedPerson = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<RentEvent> verfuegbareEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<RentEvent> rentEvents = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<Bild> bilder = new ArrayList<>();
}

