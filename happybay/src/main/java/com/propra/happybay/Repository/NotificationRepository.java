package com.propra.happybay.Repository;

import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    void deleteByGeraetIdAndAnfragePerson(Long id, String username);

    List<Notification> findAllByBesitzer(Person besitzer);
}
