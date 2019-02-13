package com.propra.happybay.Service;

import com.propra.happybay.Model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (person.getUsername().length() < 6 || person.getUsername().length() > 32) {
            errors.rejectValue("username", "Size.person.username");
        }
        if (userService.findByUsername(person.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.person.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (person.getPassword().length() < 8 || person.getPassword().length() > 32) {
            errors.rejectValue("password", "Size.person.password");
        }

        if (!person.getPasswordConfirm().equals(person.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.person.passwordConfirm");
        }
    }
}
