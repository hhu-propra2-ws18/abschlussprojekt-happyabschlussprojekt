package com.propra.happybay.Controller;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Repository.GeraetRepository;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HappyBayController {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GeraetRepository geraetRepository;
    @GetMapping("/")
    public String index(Model model){
        List<Geraet> geraete = geraetRepository.findAll();
        model.addAttribute("geraete",geraete);
        return "index";
    }


    @GetMapping("/addGeraet")
    public String addGeraet(){
        return "addGeraet";
    }

    @PostMapping("/addGeraet")
    public String addProject(@ModelAttribute(name = "geraet") Geraet geraet){
        return "change_success";
    }
}
