package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class GeraetService implements IGeraetService {

    @Autowired
    GeraetRepository geraetRepository;

    private List<Geraet> setEncode(List<Geraet> geraets){
        for (Geraet geraet: geraets){
            if(geraet.getBilder().size()==0){
                geraet.setBilder(null);
            }
            if(geraet.getBilder()!=null && geraet.getBilder().size()>0){
                geraet.setEncode(encodeBild(geraet.getBilder().get(0)));
            }
        }
        return geraets;
    }


    private String encodeBild(Bild bild){
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }

}

