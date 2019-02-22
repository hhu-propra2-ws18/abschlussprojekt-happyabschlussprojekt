package com.propra.happybay.Model;

import lombok.Data;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue
    private Long id;
    private String vorname;
    private String nachname;
    private String kontakt;
    private String adresse;
    private String username;
    private String password;
    @Transient
    private String passwordConfirm;
    private String role;
    private int anzahlNotifications = 0;
    private int aktionPunkte = 0;
    @OneToOne(cascade = {CascadeType.ALL})
    private Bild foto;
    private String encode;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();
}
