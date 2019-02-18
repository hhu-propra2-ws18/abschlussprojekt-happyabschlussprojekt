package com.propra.happybay.Model;

import lombok.Data;
import org.hibernate.annotations.Cascade;


import javax.persistence.*;
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
    private String color;

    @OneToOne(cascade = {CascadeType.ALL})
    Bild foto;

    String encode;


    public Person(String vorname, String nachname, String kontakt, String adresse, String username, String password, String passwordConfirm, String role, String color, Bild foto, String encode) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.kontakt = kontakt;
        this.adresse = adresse;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.role = role;
        this.color = color;
        this.foto = foto;
        this.encode = encode;
    }



}
