package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Reservation {
    @Id
    private Long id;
    private Double amount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Account account;
}
