package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Reservation {
    @Id
    private int id;
    private Double amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account account;
}
