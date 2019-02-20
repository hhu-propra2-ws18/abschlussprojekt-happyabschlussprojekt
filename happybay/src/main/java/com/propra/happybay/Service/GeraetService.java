package com.propra.happybay.Service;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeraetService {
    @Autowired
    GeraetRepository geraetRepository;

    public Geraet getById(Long Id) {
        return geraetRepository.findById(Id).get();
    }

}
