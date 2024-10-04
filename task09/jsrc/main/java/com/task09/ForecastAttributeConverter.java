package com.task09;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ForecastAttributeConverter implements AttributeConverter<WeatherForecast.Forecast> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AttributeValue transformFrom(WeatherForecast.Forecast input) {
        try {
            String json = objectMapper.writeValueAsString(input);
            return AttributeValue.builder().s(json).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Forecast to AttributeValue", e);
        }
    }

    @Override
    public WeatherForecast.Forecast transformTo(AttributeValue input) {
        try {
            return objectMapper.readValue(input.s(), WeatherForecast.Forecast.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert AttributeValue to Forecast", e);
        }
    }

    @Override
    public EnhancedType<WeatherForecast.Forecast> type() {
        return EnhancedType.of(WeatherForecast.Forecast.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

