package com.propra.happybay.Repository;

import com.propra.happybay.Model.Bild;
import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.Person;
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
public class GeraetRepositoryTest {
    @Autowired
    GeraetRepository repo;


    @Test
    public void testGereatFindAll(){
        Bild b1 = new Bild();
        List<Bild> bilds = new ArrayList<>();
        bilds.add(b1);

        Geraet g1 = new Geraet("a1","new geraet",true,"anton","tony",100,100,
                100,"unistr.1",new Date(12,12,12),"aa","rr",bilds);

        repo.save(g1);

        List<Geraet> geraets = repo.findAll();

        Assertions.assertThat(geraets.get(0).getId()).isEqualTo(g1.getId());

    }

    @Test
    public void testGeraetFindAllByBesitzer(){
        Bild b1 = new Bild();
        List<Bild> bilds = new ArrayList<>();
        bilds.add(b1);

        Geraet g1 = new Geraet("a1","new geraet",true,"anton","tony",100,100,
                100, "unistr.1",new Date(12,12,12),"aa","rr",bilds);

        repo.save(g1);

        List<Geraet> geraets = repo.findAllByBesitzer("anton");

        Assertions.assertThat(geraets.get(0).getId()).isEqualTo(g1.getId());

    }

    @Test
    public void testGeraetFindAllByMieter(){

        Bild b1 = new Bild();
        List<Bild> bilds = new ArrayList<>();
        bilds.add(b1);

        Geraet g1 = new Geraet("a1","new geraet",true,"anton","tony",100,100,
                100,"unistr.1",new Date(12,12,12),"aa","rr",bilds);

        repo.save(g1);

        List<Geraet> geraets = repo.findAllByMieter("tony");

        Assertions.assertThat(geraets.get(0).getId()).isEqualTo(g1.getId());
    }

    @Test
    public void testGeraetDeleteByID(){
        Bild b1 = new Bild();
        List<Bild> bilds = new ArrayList<>();
        bilds.add(b1);

        Geraet g1 = new Geraet("a1","new geraet",true,"anton","tony",100,100,
                100,"unistr.1",new Date(12,12,12),"aa","rr",bilds);

        repo.save(g1);

        List<Geraet> geraets = repo.findAll();
        Assertions.assertThat(geraets.get(0).getId()).isEqualTo(g1.getId());

        repo.deleteById(g1.getId());
        assertEquals(geraets.isEmpty(), true);
    }





}