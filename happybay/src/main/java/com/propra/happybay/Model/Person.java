package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
public class Person {
    @Id
    Long id;
    String name;
    String kontakt;
    String adresse;
    List<Geraet> verleihen;
    List<Geraet> ausleihen;
}
