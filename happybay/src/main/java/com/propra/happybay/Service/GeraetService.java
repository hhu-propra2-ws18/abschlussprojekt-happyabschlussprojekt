package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeraetService {

    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    PictureService pictureService;

    public void restoreToDefault(Long geraetId) {
        Geraet geraet = geraetRepository.findById(geraetId).get();
        geraet.setReturnStatus(ReturnStatus.DEFAULT);
        geraet.setVerfuegbar(true);
        geraet.setMieter(null);
        geraetRepository.save(geraet);
    }

    public List<Geraet> getAllWithKeyWithBiler(String key){
        return setEncode(geraetRepository.findAllByTitelLike("%"+key+"%"));

    }
    //
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
    //
    private List<Geraet> setEncode(List<Geraet> geraets){
        for (Geraet geraet: geraets){
            if (geraet.getBilder().get(0).getBild().length > 0) {
                geraet.setEncode(geraet.getBilder().get(0).encodeBild());
            }
        }
        return geraets;
    }

    public void saveNewGeraet(MultipartFile[] files, String signedInPersonUsername, Geraet geraet) throws IOException {
        List<Bild> bilder = pictureService.returnListOfPictures(files);
        geraet.setBilder(bilder);
        geraet.setVerfuegbar(true);
        geraet.setLikes(0);
        geraet.setBesitzer(signedInPersonUsername);

        RentEvent availableRentEvent = new RentEvent();
        TimeInterval availableTimeInterval = new TimeInterval(geraet.getMietezeitpunktStart(), geraet.getMietezeitpunktEnd());
        availableRentEvent.setTimeInterval(availableTimeInterval);

        geraetRepository.save(geraet);
        geraet.getVerfuegbareEvents().add(availableRentEvent);
        geraetRepository.save(geraet);
    }
}
