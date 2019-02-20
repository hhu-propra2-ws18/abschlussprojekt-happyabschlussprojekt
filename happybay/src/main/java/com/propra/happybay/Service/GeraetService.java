package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GeraetService implements IGeraetService {

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
        if(bilds.size()>0){
            for(int i=1;i<bilds.size();i++){
                encodes.add(bilds.get(i).encodeBild());
            }
            geraet.setEncode(bilds.get(0).encodeBild());
        }

        if(geraet.getBilder().size()==0){
            geraet.setBilder(null);
        }
        return encodes;
    }

    private List<Geraet> setEncode(List<Geraet> geraets){
        for (Geraet geraet: geraets){
            if(geraet.getBilder().size()==0){
                geraet.setBilder(null);
            }
            if(geraet.getBilder()!=null && geraet.getBilder().size()>0){
                geraet.setEncode(geraet.getBilder().get(0).encodeBild());
            }
        }
        return geraets;
    }

}

