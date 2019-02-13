package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue
    private Long id;
    private String role;
    @ManyToMany(mappedBy = "roles")
    private Set<Person> users;
}
