package co.empathy.academy.search.services.search;

import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import co.empathy.academy.search.responses.SearchDtoResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    public SearchDtoResponse getQuery(String searchText, String genres, String types, String ranges) {
        if (searchText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The query cannot be empty.");
        }
        SearchRequest request = new SearchRequest("imdb");

        SearchSourceBuilder requestBuilder = new SearchSourceBuilder();
        SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder(searchText, genres, types, ranges, requestBuilder);
        requestBuilder.query(searchQueryBuilder.buildQuery());
        searchQueryBuilder.addAggregations();
        request.source(requestBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();
            List<SearchHit> searchHits = List.of(hits.getHits());
            List<Map<String, Object>> titles = new ArrayList<>();
            for (SearchHit sh : searchHits) {
                titles.add(sh.getSourceAsMap());
            }
            long total = hits.getTotalHits().value;

            Map<String, Map<String, Long>> termsAggList = new HashMap<>();
            termsAggList.put("genres", getTermAggregation("genres", response));
            termsAggList.put("types", getTermAggregation("type", response));
            termsAggList.put("ranges", getRangeAggregation(response));

            return new SearchDtoResponse(total, titles, termsAggList);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<String, Long> getTermAggregation(String key, SearchResponse response) {
        Terms terms = response.getAggregations().get(key);
        Map<String, Long> dto = new HashMap<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            dto.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return dto;
    }

    private Map<String, Long> getRangeAggregation(SearchResponse response) {
        Range range = response.getAggregations().get("ranges");
        Map<String, Long> dto = new HashMap<>();
        for (Range.Bucket bucket : range.getBuckets()) {
            dto.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return dto;
    }

    public String getClusterName() {
        String clusterName;
        try {
            var request = new ClusterGetSettingsRequest();
            request.includeDefaults(true);
            clusterName = client.cluster().getSettings(request, RequestOptions.DEFAULT)
                    .getSetting("cluster.name");
        }
        catch(IOException e) {
            throw new ClusterNotFoundException(e);
        }
        return clusterName;
    }

}
