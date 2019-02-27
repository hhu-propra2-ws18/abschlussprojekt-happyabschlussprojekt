package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    private int amount;
    @OneToOne(cascade = CascadeType.ALL)
    private Person receiver;
    @OneToOne(cascade = CascadeType.ALL)
    private Person giver;
}
