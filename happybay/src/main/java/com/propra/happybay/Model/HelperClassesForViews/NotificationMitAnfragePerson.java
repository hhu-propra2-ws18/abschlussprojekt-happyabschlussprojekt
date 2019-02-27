package com.propra.happybay.Model.HelperClassesForViews;

import com.propra.happybay.Model.Notification;
import com.propra.happybay.Model.Person;
import lombok.Data;

@Data
public class NotificationMitAnfragePerson {
    Notification notification;
    Person anfragePerson;
}
