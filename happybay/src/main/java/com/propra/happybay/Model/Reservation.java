package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Reservation {
    @Id
    private Long id;



    private Double amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account account;


    public Reservation(Long id, Double amount) {
        this.id = id;
        this.amount = amount;
    }
}
