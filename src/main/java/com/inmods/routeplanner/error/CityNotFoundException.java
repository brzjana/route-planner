package com.inmods.routeplanner.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such City")
public class CityNotFoundException extends RuntimeException{
    public CityNotFoundException(String name){
        super("City \"" + name + "\"");
    }
}