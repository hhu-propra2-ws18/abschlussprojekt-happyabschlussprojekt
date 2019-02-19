package com.propra.happybay.Repository;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Notification;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest

public class NotificationRepositoryTest {

    @Autowired
    NotificationRepository repo;

    @Test
    public void testNotificationFindByGeraetID(){

        Bild b1 = new Bild();
        List<Bild> bilds = new ArrayList<>();
        bilds.add(b1);
        Geraet g1 = new Geraet("a1","new geraet",true,"anton","tony",100,100,
                100,"unistr.1",new Date(12,12,12),"aa","rr",bilds);


        Notification n1 = new Notification(g1.getId(),"n1","tony","anton"
                ,new Date(12,12,12),22);
        repo.save(n1);
        List<Notification> notifications = repo.findByGeraetId(g1.getId());

        Assertions.assertThat(notifications.get(0).getId()).isEqualTo(n1.getId());
    }
}