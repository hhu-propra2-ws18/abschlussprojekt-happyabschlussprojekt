package com.propra.happybay.NormalObject;

import lombok.Data;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Data
public class BindingResultWithErrors {
    List<String> errorList = new ArrayList<>();
    BindingResult bindingResult;
    public BindingResultWithErrors(BindingResult bindingResult) {
        this.bindingResult=bindingResult;
    }

    public void findErrorList() {
        for (int i=0; i< bindingResult.getAllErrors().size(); i++){
            errorList.add(bindingResult.getAllErrors().get(i).getCode());
        }
    }
}
