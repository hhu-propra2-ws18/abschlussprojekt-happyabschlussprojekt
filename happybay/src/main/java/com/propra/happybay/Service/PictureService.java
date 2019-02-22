package com.propra.happybay.Service;

import com.propra.happybay.Model.Bild;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PictureService {
    public List<Bild> returnListOfPictures(MultipartFile[] files) throws IOException {
        List<Bild> bilder = new ArrayList<>();
        for (MultipartFile file : files) {
            bilder.add(getBildFromInput(file));
        }
        return bilder;
    }

    public Bild getBildFromInput(MultipartFile file) throws IOException {
        Bild bild = new Bild();
        bild.setBild(file.getBytes());
        return bild;
    }
}
