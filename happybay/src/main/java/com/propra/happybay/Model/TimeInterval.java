package com.propra.happybay.Model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Date;

@Data
@Embeddable
public class TimeInterval {
    private Date start;

    private Date end;

    private int duration;


    public TimeInterval(Date start, Date end) {
        this.start = start;
        this.end = end;
        Long milisecond = end.getTime() - start.getTime();
        this.duration = Math.round(milisecond / (1000 * 60 * 60 * 24));
    }

    public TimeInterval() {
    }
}
