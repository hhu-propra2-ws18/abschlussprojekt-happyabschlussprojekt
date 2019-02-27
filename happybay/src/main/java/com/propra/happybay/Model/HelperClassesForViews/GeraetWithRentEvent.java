package com.propra.happybay.Model.HelperClassesForViews;

import com.propra.happybay.Model.Geraet;
import com.propra.happybay.Model.RentEvent;
import lombok.Data;

@Data
public class GeraetWithRentEvent {
    Geraet geraet;
    RentEvent rentEvent;
}
