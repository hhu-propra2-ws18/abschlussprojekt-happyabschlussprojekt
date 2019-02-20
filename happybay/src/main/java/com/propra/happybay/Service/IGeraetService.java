package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;

import java.util.List;

public interface IGeraetService {
    List<Geraet> getAllWithKeyWithBiler(String key);
    List<Geraet> getAllByBesitzerWithBilder(String name);
    List<Geraet> getAllByMieterWithBilder(String name);
    List<String> geraetBilder(Geraet geraet);
}
