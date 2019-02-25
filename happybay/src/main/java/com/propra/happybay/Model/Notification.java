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
    Long rentEventId;
    String type;
    String message;
    String besitzer;
    String anfragePerson;
    Date mietezeitpunktStart;
    Date mietezeitpunktEnd;
    String encode;
}

