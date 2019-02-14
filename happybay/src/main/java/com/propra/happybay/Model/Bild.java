package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Blob;

@Data
@Entity
public class Bild {
    @Id
    @GeneratedValue
    Long id;
    Blob bild;
}
