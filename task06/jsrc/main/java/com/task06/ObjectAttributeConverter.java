package com.task06;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.stream.Collectors;

public class ObjectAttributeConverter implements AttributeConverter<Object> {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public AttributeValue transformFrom(Object input) {
        if (input instanceof String) {
            return AttributeValue.builder().s((String) input).build();
        } else if (input instanceof Map) {
            return AttributeValue.builder().m(((Map<String, AttributeValue>) input).entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> transformFrom(entry.getValue())))).build();
        } else if (input instanceof Number) {
            return AttributeValue.builder().n(input.toString()).build();
        }
        throw new IllegalArgumentException("Unsupported type: " + input.getClass().getName());
    }

    @Override
    public Object transformTo(AttributeValue attributeValue) {
        if (attributeValue.s() != null) {
            return attributeValue.s();
        } else if (attributeValue.m() != null) {
            return attributeValue.m().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        AttributeValue value = entry.getValue();
                        if (value.s() != null) {
                            return value.s();
                        } else if (value.n() != null) {
                            return value.n();
                        }
                        throw new IllegalArgumentException("Unsupported AttributeValue type");
                    }));
        }

        throw new IllegalArgumentException("Unsupported AttributeValue type");
    }

    @Override
    public EnhancedType<Object> type() {
        return EnhancedType.of(Object.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.M;
    }
}

