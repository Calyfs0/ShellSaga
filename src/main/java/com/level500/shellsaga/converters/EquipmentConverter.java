package com.level500.shellsaga.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.level500.shellsaga.core.game.items.Equipment;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Converter(autoApply = true)
public class EquipmentConverter implements AttributeConverter<List<Equipment>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Equipment> equipmentList) {
        try {
            return objectMapper.writeValueAsString(equipmentList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing equipment list", e);
        }
    }

    @Override
    public List<Equipment> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Equipment.class));
        } catch (IOException e) {
            return new ArrayList<>(); // Return empty list if JSON conversion fails
        }
    }
}
