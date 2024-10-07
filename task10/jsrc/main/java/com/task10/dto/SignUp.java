
package com.task10.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Pattern;

public record SignUp(String email, String password, String firstName, String lastName) {
    static Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public SignUp {
        if (email == null || password == null || firstName == null || lastName == null) {

            throw new IllegalArgumentException("Missing or incomplete data.");
        }
        if (!emailPattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email address.");
        }
    }

    public static SignUp fromJson(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, SignUp.class);
    }

}
