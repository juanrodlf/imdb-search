package co.empathy.academy.search.services;

import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import co.empathy.academy.search.responses.SearchDtoResponse;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    public SearchDtoResponse getQuery(String searchText, String genres) {
        if (searchText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The query cannot be empty.");
        }
        SearchRequest request = new SearchRequest("imdb");

        SearchSourceBuilder requestBuilder = new SearchSourceBuilder();
        requestBuilder.query(buildQuery(searchText, genres));
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
            return new SearchDtoResponse(total, titles);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private QueryBuilder buildQuery(String searchText, String genres) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("primaryTitle", searchText));
        if (genres != null) {
            String[] genresSplit = genres.split(",");
            for (String s : genresSplit) {
                queryBuilder.must(QueryBuilders.matchQuery("genres", s));
            }
        }
        return queryBuilder;
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
