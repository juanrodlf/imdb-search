package co.empathy.academy.search.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record Title(String tConst,
                    String titleType,
                    String primaryTitle,
                    String originalTitle,
                    Boolean isAdult,
                    Integer startYear,
                    Integer endYear,
                    Integer runtimeMinutes,
                    List<String> genres) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String getAsString(Title title) {
        try {
            return OBJECT_MAPPER.writeValueAsString(title);
        } catch(JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
