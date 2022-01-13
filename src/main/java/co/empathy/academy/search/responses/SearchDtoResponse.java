package co.empathy.academy.search.responses;

import java.util.List;
import java.util.Map;

public record SearchDtoResponse(long total, List<Map<String, Object>> items, Map<String, Long> genresToDto) {
}
