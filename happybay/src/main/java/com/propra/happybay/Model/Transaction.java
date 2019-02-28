package com.propra.happybay.Model;

import com.propra.happybay.TransactionType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue
    private Long id;
    private int amount;
    private TransactionType transactionType;

    @ManyToOne
    private Person receiver;
    @ManyToOne
    private Person giver;
}
