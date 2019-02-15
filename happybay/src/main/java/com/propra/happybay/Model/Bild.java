package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Base64;

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


    public String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }
}
