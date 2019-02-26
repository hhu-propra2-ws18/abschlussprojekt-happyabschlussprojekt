package com.propra.happybay.Repository;

import com.propra.happybay.Model.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification,Long> {
    void deleteByGeraetIdAndAnfragePerson(Long id,String username);
    List<Notification> findByGeraetId(Long id);
    List<Notification> findAllByGeraetId(Long id);
    List<Notification> findAllByBesitzer(String besitzer);

}
