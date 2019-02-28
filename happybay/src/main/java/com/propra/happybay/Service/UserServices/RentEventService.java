package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Repository.RentEventRepository;
import com.propra.happybay.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentEventService {
    @Autowired
    private RentEventRepository rentEventRepository;

    public List<RentEvent> getActiveEventsForPerson(Person person) {
        List<RentEvent> activeRentEvents = rentEventRepository.findAllByMieterAndReturnStatus(person, ReturnStatus.ACTIVE);
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(person, ReturnStatus.DEADLINE_CLOSE));
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(person, ReturnStatus.DEADLINE_OVER));
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(person, ReturnStatus.KAPUTT));
        activeRentEvents.addAll(rentEventRepository.findAllByMieterAndReturnStatus(person, ReturnStatus.WAITING_FOR_CONFIRMATION));
        return activeRentEvents;
    }
}
