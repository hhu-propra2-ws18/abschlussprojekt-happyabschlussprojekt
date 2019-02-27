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
    private Long id;
    private Long personId;
    private String geraetTitel;
    private String message;
    private String senderFrom;
    private LocalDate date;

}

