package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.sql.Date;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class RentEventServiceTest {

    RentEventService rentEventService;
    @Before
    public void setUp() {
        rentEventService = new RentEventService();
    }
    @Test
    public void calculatePriceTest() {
        Date start = new Date(2019, 01, 01);
        Date end = new Date(2019, 1, 5);
        TimeInterval timeInterval = new TimeInterval(start, end);
        Geraet geraet = new Geraet();
        geraet.setKosten(10);
        RentEvent rentEvent = new RentEvent();
        rentEvent.setGeraet(geraet);
        rentEvent.setTimeInterval(timeInterval);
        assertEquals(rentEventService.calculatePrice(rentEvent), 40, 0.1);
    }
}
