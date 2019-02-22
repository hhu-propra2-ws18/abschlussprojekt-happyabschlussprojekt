package com.propra.happybay.NormalObject;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class GeraetMitZeit {
    @Autowired
    GeraetRepository geraetRepository;
    List<Geraet> rentThings;
    List<Geraet> remindRentThings = new ArrayList<>();
    List<Geraet> overTimeThings = new ArrayList<>();
    LocalDate deadLine = LocalDate.now().plusDays(4);


    public GeraetMitZeit(List<Geraet> rentThings) {
        this.rentThings=rentThings;
    }

    public void pruefGeraetZeit() {
        for (Geraet geraet : rentThings) {
            if (geraet.getEndzeitpunkt().isBefore(deadLine) || geraet.getEndzeitpunkt().isEqual(deadLine)) {
                if (LocalDate.now().isAfter(geraet.getEndzeitpunkt())) {
                    overTimeThings.add(geraet);
                } else {
                    remindRentThings.add(geraet);
                }
            }
        }
    }

}
