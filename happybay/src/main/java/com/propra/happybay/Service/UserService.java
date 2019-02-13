package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;

public interface UserService {
    public void save(Person person);
    public Person findByUsername(String username);
}
