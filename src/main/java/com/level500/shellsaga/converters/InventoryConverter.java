package com.level500.shellsaga.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter
public class InventoryConverter implements AttributeConverter<Map<String, Integer>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Integer> inventory) {
        try {
            return objectMapper.writeValueAsString(inventory);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert inventory to JSON", e);
        }
    }

    @Override
    public Map<String, Integer> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, HashMap.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to inventory", e);
        }
    }
}

