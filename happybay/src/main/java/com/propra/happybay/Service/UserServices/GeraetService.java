package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.*;
import com.propra.happybay.Model.HelperClassesForViews.GeraetWithRentEvent;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class GeraetService {

    @Autowired
    GeraetRepository geraetRepository;
    @Autowired
    PictureService pictureService;
    @Autowired
    RentEventRepository rentEventRepository;
    @Autowired
    PersonService personService;

    public List<Geraet> getAllWithKeyWithBiler(String key) {
        return setEncode(geraetRepository.findAllByTitelLike("%" + key + "%"));
    }

    public List<Geraet> getAllByBesitzerWithBilder(Person besitzer) {
        return setEncode(geraetRepository.findAllByBesitzer(besitzer));
    }

    public List<Geraet> getAllWithFilterPreisAufsteigendWithBilder(String key) {
        return setEncode(geraetRepository.findAllByTitelLikeOrderByKostenAsc("%" + key + "%"));
    }

    public List<Geraet> getAllWithFilterPreisAbsteigendWithBilder(String key) {
        return setEncode(geraetRepository.findAllByTitelLikeOrderByKostenDesc("%" + key + "%"));
    }

    public List<Geraet> getAllWithFilterLikeAufsteigendWithBilder(String key) {
        return setEncode(geraetRepository.findAllByTitelLikeOrderByLikesAsc("%" + key + "%"));
    }

    public List<Geraet> getAllWithFilterLikeAbsteigendWithBilder(String key) {
        return setEncode(geraetRepository.findAllByTitelLikeOrderByLikesDesc("%" + key + "%"));
    }

    public List<String> geraetBilder(Geraet geraet) {
        List<Bild> bilds = geraet.getBilder();
        List<String> encodes = new ArrayList<>();
        for (int i = 1; i < bilds.size(); i++) {
            encodes.add(bilds.get(i).encodeBild());
        }
        if (geraet.getBilder().get(0).getBild().length > 0) {
            geraet.setEncode(geraet.getBilder().get(0).encodeBild());
        }
        return encodes;
    }

    private List<Geraet> setEncode(List<Geraet> geraets) {
        for (Geraet geraet : geraets) {
            if (geraet.getBilder().get(0).getBild().length > 0) {
                geraet.setEncode(geraet.getBilder().get(0).encodeBild());
            }
        }
        return geraets;
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
            geraetWithRentEvent.setGeraet(rentEvent.getGeraet());
            geraetWithRentEvent.setRentEvent(rentEvent);
            overTimeThings.add(geraetWithRentEvent);
        }
        return overTimeThings;
    }

    public TimeInterval convertToCET(TimeInterval timeInterval) {
        Date start = new Date(timeInterval.getStart().getTime() + 60 * 60 * 6000);
        Date end = new Date(timeInterval.getEnd().getTime() + 60 * 60 * 6000);
        TimeInterval newTimeInterval = new TimeInterval(start, end);
        return newTimeInterval;
    }

    public void editGeraet(MultipartFile[] files, Geraet geraet, Long id, boolean isForSale) throws IOException {
        Geraet geraet1 = geraetRepository.findById(id).get();
        List<Bild> bilds = new ArrayList<>();
        personService.umwechsleMutifileZumBild(files, bilds);
        geraet1.setBilder(bilds);
        geraet1.setKosten(geraet.getKosten());
        geraet1.setTitel(geraet.getTitel());
        geraet1.setBeschreibung(geraet.getBeschreibung());
        geraet1.setForsale(isForSale);
        geraet1.setKaution(geraet.getKaution());
        geraet1.setAbholort(geraet.getAbholort());
        geraetRepository.save(geraet1);
    }

    public void addLike(Long id, Person person) {
        Geraet geraet = geraetRepository.findById(id).get();
        List<Person> likedPerson = geraet.getLikedPerson();
        boolean hasAlreadyLiked = false;
        for (int i = 0; i < likedPerson.size(); i++) {
            if (likedPerson.get(i).getUsername().equals(person.getUsername())) {
                hasAlreadyLiked = true;
            }
        }
        if (!hasAlreadyLiked) {
            geraet.getLikedPerson().add(person);
            geraet.setLikes(geraet.getLikes() + 1);
        } else {
            geraet.getLikedPerson().remove(person);
            geraet.setLikes(geraet.getLikes() - 1);
        }
        geraetRepository.save(geraet);
    }
}
