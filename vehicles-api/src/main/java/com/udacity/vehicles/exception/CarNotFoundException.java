package com.udacity.vehicles.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(Long id) {
        super(String.format("Car not found with id %d", id));
    }
}
