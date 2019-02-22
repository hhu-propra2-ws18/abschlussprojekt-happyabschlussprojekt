package com.propra.happybay.Service.AdminServices;

import com.propra.happybay.Model.InformationForMenuBadges;
import com.propra.happybay.Model.PersonMitAccount;
import org.reactivestreams.Publisher;

import java.util.List;

public interface AdminService {
    List<PersonMitAccount> returnAllPersonsWithAccounts();

    InformationForMenuBadges returnInformationForMenuBadges();

    boolean isAdminHasDefaultPassword();

    void changeAdminPassword(String newPassword);
}
