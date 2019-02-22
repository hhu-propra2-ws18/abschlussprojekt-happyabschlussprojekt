package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class TransferRequest {
    @Id
    @GeneratedValue
    Long id;
    String username;
    int amount = 0;

    public TransferRequest(String username, int amount) {
        this.username = username;
        this.amount = amount;
    }

    public TransferRequest() {}
}

