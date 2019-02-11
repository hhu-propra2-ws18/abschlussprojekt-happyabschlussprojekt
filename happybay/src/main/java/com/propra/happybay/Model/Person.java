package com.propra.happybay.Model;

import lombok.Data;
import org.hibernate.annotations.Cascade;


import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    Long id;
    String name;
    String password;
    String kontakt;
    String adresse;
    @OneToMany(cascade = CascadeType.ALL)
    List<Geraet> verleihen;
    @OneToMany(cascade = CascadeType.ALL)
    List<Geraet> ausleihen;
}
