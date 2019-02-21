package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeraetService {

    @Autowired
    GeraetRepository geraetRepository;

    public List<Geraet> getAllWithKeyWithBiler(String key){
        return setEncode(geraetRepository.findAllByTitelLike("%"+key+"%"));

    }

    public List<Geraet> getAllByBesitzerWithBilder(String name){
        return setEncode(geraetRepository.findAllByBesitzer(name));
    }

    public List<Geraet> getAllByMieterWithBilder(String name){
        return setEncode(geraetRepository.findAllByMieter(name));
    }

    public List<String> geraetBilder(Geraet geraet){
        List<Bild> bilds = geraet.getBilder();
        List<String> encodes = new ArrayList<>();
        for(int i=1;i<bilds.size();i++){
            encodes.add(bilds.get(i).encodeBild());
        }
        if (geraet.getBilder().get(0).getBild().length > 0) {
            geraet.setEncode(geraet.getBilder().get(0).encodeBild());
        }
        return encodes;
    }

    private List<Geraet> setEncode(List<Geraet> geraets){
        for (Geraet geraet: geraets){
            if (geraet.getBilder().get(0).getBild().length > 0) {
                geraet.setEncode(geraet.getBilder().get(0).encodeBild());
            }
        }
        return geraets;
@Service
public class GeraetService {
    @Autowired
    GeraetRepository geraetRepository;

    public Geraet getById(Long Id) {
        return geraetRepository.findById(Id).get();
    }

}
