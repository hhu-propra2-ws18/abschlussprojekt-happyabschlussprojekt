package com.propra.happybay.Model;

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
