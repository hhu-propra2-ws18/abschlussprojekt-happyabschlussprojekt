package com.propra.happybay.Service.DefaultServices;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.NotificationRepository;
import com.propra.happybay.Repository.PersonRepository;
import com.propra.happybay.Service.ProPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DefaultService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    public PasswordEncoder encoder;


    public String encodeBild(Bild bild) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bild.getBild());
        return encode;
    }
}
