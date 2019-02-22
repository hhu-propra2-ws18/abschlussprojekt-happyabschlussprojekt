package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue
    Long id;

    Long personId;

    String geraetTitel;

    String message;

    String senderFrom;

    LocalDate date;

}

