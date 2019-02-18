package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Account {
    @Id
    private String account;
    private Double amount;

    @OneToMany
    private List<Reservation> reservations;


    public Account(String account, Double amount, List<Reservation> reservations) {
        this.account = account;
        this.amount = amount;
        this.reservations = reservations;
    }

}
