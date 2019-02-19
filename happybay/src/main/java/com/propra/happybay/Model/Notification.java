package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Data
@Entity
public class Notification {
    @Id
    @GeneratedValue
    Long id;
    Long geraetId;
    String type;
    String message;
    String anfragePerson;
    Date mietezeitPunkt;
    int zeitraum;


}

