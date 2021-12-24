package co.empathy.academy.search.model;

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
}
