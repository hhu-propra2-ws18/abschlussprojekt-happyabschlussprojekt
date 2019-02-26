package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Service
public class GeraetService {

    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    PictureService pictureService;
    @Autowired
    RentEventRepository rentEventRepository;

    public List<Geraet> getAllWithKeyWithBiler(String key){
        return setEncode(geraetRepository.findAllByTitelLike("%"+key+"%"));
    }

    public List<Geraet> getAllByBesitzerWithBilder(String name){
        return setEncode(geraetRepository.findAllByBesitzer(name));
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

    private List<Geraet> setEncode(List<Geraet> geraets) {
        for (Geraet geraet: geraets){
            if (geraet.getBilder().get(0).getBild().length > 0) {
                geraet.setEncode(geraet.getBilder().get(0).encodeBild());
            }
        }
        return geraets;
    }

    public void checkForTouchingIntervals(Geraet geraet, RentEvent rentEvent) {
        List<RentEvent> rentEvents = geraet.getVerfuegbareEvents();
        rentEvents.add(rentEvent);

        for (int j = 0; j < rentEvents.size(); j++) {
            int smallest = j;
            for (int i = j + 1; i < rentEvents.size(); i++) {
                Long start = rentEvents.get(i).getTimeInterval().getStart().getTime();
                if (start < rentEvents.get(j).getTimeInterval().getStart().getTime()) {
                    smallest = i;
                }
            }
            Collections.swap(rentEvents, j, smallest);
        }

        for (int i = 0; i < rentEvents.size()-1; i++) {
            TimeInterval timeInterval1 = rentEvents.get(i).getTimeInterval();
            TimeInterval timeInterval2 = rentEvents.get(i + 1).getTimeInterval();
            Date timeInterval1Start = timeInterval1.getStart();
            Date timeInterval1End = timeInterval1.getEnd();
            Date timeInterval2Start = timeInterval2.getStart();

            if (timeInterval1End.compareTo(timeInterval2Start) == 0) {
                timeInterval2.setStart(timeInterval1Start);
                timeInterval1.setStart(null);
            }
        }
        List<RentEvent> toRemove = new ArrayList<>();
        for (RentEvent rentEvent1: rentEvents) {
            if (rentEvent1.getTimeInterval().getStart() == null) {
                toRemove.add(rentEvent1);
            }
        }
        rentEvents.removeAll(toRemove);
        geraetRepository.save(geraet);
    }
    @Scheduled(fixedRate = 86400000)
    public void checkRentEventStatus() {
        List<RentEvent> rentEvents = rentEventRepository.findAll();
        for (RentEvent rentEvent : rentEvents) {
            TimeInterval timeInterval = rentEvent.getTimeInterval();
            Long start = timeInterval.getStart().getTime();
            Long end = timeInterval.getEnd().getTime();
            Long now = System.currentTimeMillis();
            if (now > start) {
                rentEvent.setReturnStatus(ReturnStatus.ACTIVE);
                rentEventRepository.save(rentEvent);
            }
            if (now + (1000 * 60 * 60 * 72) - end > 0) {
                rentEvent.setReturnStatus(ReturnStatus.DEADLINE_CLOSE);
                rentEventRepository.save(rentEvent);
            }
            if (now > end) {
                rentEvent.setReturnStatus(ReturnStatus.DEADLINE_OVER);
                rentEventRepository.save(rentEvent);
            }
        }
    }

    public List<GeraetWithRentEvent> returnGeraeteWithRentEvents(List<RentEvent> rentEventsDeadlineOver) {
        List<GeraetWithRentEvent> overTimeThings = new ArrayList<>();
        for (RentEvent rentEvent : rentEventsDeadlineOver) {
            GeraetWithRentEvent geraetWithRentEvent = new GeraetWithRentEvent();
            geraetWithRentEvent.setGeraet(geraetRepository.findById(rentEvent.getGeraetId()).get());
            geraetWithRentEvent.setRentEvent(rentEvent);
            overTimeThings.add(geraetWithRentEvent);
        }
        return overTimeThings;
    }

    public TimeInterval convertToCET(TimeInterval timeInterval) {
        Date start = new Date(timeInterval.getStart().getTime() + 60 * 60 * 1000);
        Date end = new Date(timeInterval.getEnd().getTime() + 60 * 60 * 1000);
        TimeInterval newTimeInterval = new TimeInterval(start, end);
        return newTimeInterval;
    }
}
