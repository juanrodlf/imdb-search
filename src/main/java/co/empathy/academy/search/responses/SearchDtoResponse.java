package co.empathy.academy.search.responses;

import org.elasticsearch.search.SearchHit;

import java.util.List;

public record SearchDtoResponse(List<String> result) {
}
