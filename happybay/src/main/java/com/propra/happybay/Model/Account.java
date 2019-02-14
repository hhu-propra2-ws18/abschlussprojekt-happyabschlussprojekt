package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    private Long personId;
    private String account;
    private double amount;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Reservations> reservations;
}
