package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    private int amount;
    private String receiver;
    private String giver;
}
