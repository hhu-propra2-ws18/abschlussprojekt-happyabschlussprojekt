package com.propra.happybay.Model.HelperClassesForViews;

import com.propra.happybay.Model.Account;
import com.propra.happybay.Model.Person;
import lombok.Data;

@Data
public class PersonMitAccount {
    Person person;
    Account account;

    public PersonMitAccount(Person person, Account account) {
        this.person = person;
        this.account = account;
    }
}
