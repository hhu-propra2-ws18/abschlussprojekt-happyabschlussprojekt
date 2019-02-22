package com.propra.happybay.Service.UserServices;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
import com.propra.happybay.Model.RentEvent;
import com.propra.happybay.Model.TimeInterval;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private GeraetRepository geraetRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> userOptional = personRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            Person user = userOptional.get();
            UserDetails userDetails = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRole())
                    .build();
            return userDetails;
        }
        throw new UsernameNotFoundException("Invalid username");
    }

//    public boolean isOverlapping(RentEvent rentEvent1, RentEvent rentEvent2){
//        TimeInterval timeInterval1 = rentEvent1.getTimeInterval();
//        TimeInterval timeInterval2 = rentEvent2.getTimeInterval();
//        if((timeInterval1.getEnd().getTime() > timeInterval2.getStart().getTime()) || (timeInterval1.getStart().getTime() > timeInterval2.getEnd().getTime())){
//            return true;
//        }
//        return false;
//    }

    public int positionOfFreeBlock(Geraet geraet, RentEvent anfrageRentEvent) {
        TimeInterval anfrageInterval = anfrageRentEvent.getTimeInterval();
        for (int i = 0; i < geraet.getVerfuegbareEvents().size(); i++) {
            TimeInterval verfuegbar = geraet.getVerfuegbareEvents().get(i).getTimeInterval();
            if ((verfuegbar.getStart().getTime() < anfrageInterval.getStart().getTime()) && (verfuegbar.getEnd().getTime() > anfrageInterval.getEnd().getTime())) {
                return i;
            }
        }
        return -1;
    }

    public void intervalZerlegen(Geraet geraet, int index, RentEvent rentEvent) {
        TimeInterval timeInterval1 = new TimeInterval(geraet.getVerfuegbareEvents().get(index).getTimeInterval().getStart(),
                rentEvent.getTimeInterval().getStart());
        TimeInterval timeInterval2 = new TimeInterval(rentEvent.getTimeInterval().getEnd(),
                geraet.getVerfuegbareEvents().get(index).getTimeInterval().getEnd());
        RentEvent rentEvent1 = new RentEvent();
        rentEvent1.setTimeInterval(timeInterval1);

        RentEvent rentEvent2 = new RentEvent();
        rentEvent2.setTimeInterval(timeInterval2);
        geraet.getVerfuegbareEvents().add(rentEvent1);
        geraet.getVerfuegbareEvents().add(rentEvent2);
        geraet.getVerfuegbareEvents().remove(index);
        geraetRepository.save(geraet);
    }
}

