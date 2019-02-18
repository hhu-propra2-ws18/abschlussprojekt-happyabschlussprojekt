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
    @OneToMany(mappedBy = "account", cascade = {CascadeType.ALL})
    private List<Reservation> reservations;
}
