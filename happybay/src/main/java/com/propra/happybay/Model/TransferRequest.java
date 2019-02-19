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
    int amount;


}

