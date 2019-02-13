package com.propra.happybay.Model;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Length;


import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
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
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL)
    List<Geraet> verleihen;
    @OneToMany(cascade = CascadeType.ALL)
    List<Geraet> ausleihen;
}
