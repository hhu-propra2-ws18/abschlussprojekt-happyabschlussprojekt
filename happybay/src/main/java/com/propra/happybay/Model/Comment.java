package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue
    Long id;
    Long geraetId;

    String message;

    String sender;

    String empfaenger;

    Date date;


}

