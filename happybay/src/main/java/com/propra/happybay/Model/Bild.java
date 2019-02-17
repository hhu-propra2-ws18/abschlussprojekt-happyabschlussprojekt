package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;

@Data
@Entity
public class Bild {
    @Id
    @GeneratedValue
    Long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="gerat_bilder", columnDefinition="longblob", nullable=true)
    private byte[] bild;
}
