package com.propra.happybay.Service.DefaultServices;

import com.propra.happybay.Model.Bild;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class DefaultServiceTest {

    @InjectMocks
    DefaultService defaultService;

    @Test
    public void encode_bild() {
        Bild fakeBild = new Bild();
        fakeBild.setBild("fake Bild".getBytes());

        Base64.Encoder encoder = Base64.getEncoder();
        String testencode = encoder.encodeToString("fake Bild".getBytes());
        Assert.assertEquals(defaultService.encodeBild(fakeBild), testencode);
    }

}