package com.propra.happybay.Repository;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification,Long> {


    List<Notification> findByGeraetId(Long id);
}
