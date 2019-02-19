package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;
import com.propra.happybay.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private PersonRepository personRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Das Feld 'Benutzername' muss ausgefüllt werden.");
        if (person.getUsername().length() < 1 || person.getUsername().length() > 32) {
            errors.rejectValue("username", "Bitte wählen Sie zwischen 6 und 32 Zeichen.");
        }
        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            errors.rejectValue("username", "Dieser Benutzername ist leider schon vergeben.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Das Feld 'Password' muss ausgefüllt werden.");
        if (person.getPassword().length() < 1 || person.getPassword().length() > 32) {
            errors.rejectValue("password", "Bitte wählen Sie ein Password mit mindestens 8 Zeichen.");
        }

        if (!person.getPasswordConfirm().equals(person.getPassword())) {
            errors.rejectValue("passwordConfirm", "Passwörter stimmen nicht überein.");
        }
    }
}
