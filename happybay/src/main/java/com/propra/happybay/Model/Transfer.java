package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Transfer {
    @Id
    @GeneratedValue
    Long id;
    String absender;
    String empfänger;
    int amount;


    public Transfer(String absender, String empfänger, int amount) {
        this.absender = absender;
        this.empfänger = empfänger;
        this.amount = amount;
    }
}
