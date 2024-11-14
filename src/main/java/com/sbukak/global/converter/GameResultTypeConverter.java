package com.sbukak.global.converter;

import com.sbukak.global.enums.GameResultType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;

@Converter
public class GameResultTypeConverter implements AttributeConverter<List<GameResultType>, String> {
    private static final String SPLIT_CHAR = ", ";

    @Override
    public String convertToDatabaseColumn(List<GameResultType> types) {
        return String.join(SPLIT_CHAR, types.stream().map(Enum::name).toList());
    }

    @Override
    public List<GameResultType> convertToEntityAttribute(String string) {
        return Arrays.stream(string.split(SPLIT_CHAR)).map(GameResultType::valueOf).toList();
    }
}
