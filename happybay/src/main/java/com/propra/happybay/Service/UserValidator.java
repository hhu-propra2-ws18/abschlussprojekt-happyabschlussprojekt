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
    //@Autowired
    //private UserService userService;
    @Autowired
    private PersonRepository personRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "This field Username is required.");
        if (person.getUsername().length() < 1 || person.getUsername().length() > 32) {
            errors.rejectValue("username", "Please use between 6 and 32 characters.");
        }
        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            errors.rejectValue("username", "Someone already has that username.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "This field Password is required.");
        if (person.getPassword().length() < 1 || person.getPassword().length() > 32) {
            errors.rejectValue("password", "Try one with at least 8 characters.");
        }

        if (!person.getPasswordConfirm().equals(person.getPassword())) {
            errors.rejectValue("passwordConfirm", "These passwords don't match.");
        }
    }
}
