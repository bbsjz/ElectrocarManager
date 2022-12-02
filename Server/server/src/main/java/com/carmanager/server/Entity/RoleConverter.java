package com.carmanager.server.Entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class RoleConverter implements AttributeConverter<List<String>,String> {
    @Override
    public String convertToDatabaseColumn(List<String> list)
    {
        return String.join(",",list);
    }

    @Override
    public List<String> convertToEntityAttribute(String s)
    {
        if(s.equals(""))
        {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }
}
