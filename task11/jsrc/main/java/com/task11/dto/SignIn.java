
package com.task11.dto;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public record SignIn(String email, String password) {

    public SignIn {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Missing or incomplete data.");
        }
    }

    public static SignIn fromJson(String jsonString) throws JsonProcessingException {
        return new ObjectMapper().readValue(jsonString, SignIn.class);
    }

}
