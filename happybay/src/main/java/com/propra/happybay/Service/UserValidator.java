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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (person.getUsername().length() < 1 || person.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.person.username");
        }
        if (personRepository.findByUsername(person.getUsername()).isPresent()) {
            errors.rejectValue("username", "Duplicate.person.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (person.getPassword().length() < 1 || person.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.person.password");
        }

        if (!person.getPasswordConfirm().equals(person.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.person.passwordConfirm");
        }
    }
}
