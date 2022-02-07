package co.empathy.academy.search.services.search;

import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import co.empathy.academy.search.responses.SearchDtoResponse;
import co.empathy.academy.search.services.search.exceptions.ElasticUnavailableException;
import co.empathy.academy.search.services.search.exceptions.EmptyQueryException;
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
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * Searches for the current query
     * @param searchText Text you want to be searched
     * @param genres Genres to filter the query, separated by commas (if any)
     * @param types Types to filter the query, separated by commas (if any)
     * @param ranges Ranges to filter the query, separated by commas and slashes (if any)
     * @param start Number of titles to be skipped
     * @param rows Number of items per page (to be shown in results)
     * @return The result of the search process
     */
    public SearchDtoResponse search(String searchText, String genres, String types, String ranges, int start, int rows) throws EmptyQueryException, ElasticUnavailableException {
        if (searchText.trim().isEmpty()) {
            throw new EmptyQueryException();
        }
        SearchRequest request = new SearchRequest("imdb");

        SearchSourceBuilder requestBuilder = new SearchSourceBuilder();
        SearchQueryBuilder searchQueryBuilder = new SearchQueryBuilder(searchText, genres, types, ranges, requestBuilder);
        requestBuilder.query(searchQueryBuilder.buildQuery());
        requestBuilder.from(start);
        if (rows != 0) {
            requestBuilder.size(rows);
        }
        else {
            requestBuilder.size(10);
        }
        searchQueryBuilder.addAggregations();
        request.source(requestBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            SearchHits hits = response.getHits();
            List<SearchHit> searchHits = List.of(hits.getHits());
            List<Map<String, Object>> titles = new ArrayList<>();
            long total;
            if (searchHits.size() == 0) {
                SuggestQueryBuilder sqb = new SuggestQueryBuilder(searchText);
                SearchSourceBuilder searchSourceBuilder= sqb.addSuggestionsPhrase();
                request.source(searchSourceBuilder);
                response = client.search(request, RequestOptions.DEFAULT);
                total = 0;
                return new SearchDtoResponse(total, new ArrayList<>(), new HashMap<>(), getSuggestions(response));
            }
            else {
                for (SearchHit sh : searchHits) {
                    Map<String, Object> title = sh.getSourceAsMap();
                    title.put("score", sh.getScore());
                    titles.add(title);
                }
                total = hits.getTotalHits().value;

                Map<String, Map<String, Long>> termsAggList = new HashMap<>();
                termsAggList.put("genres", getTermAggregation("genres", response));
                termsAggList.put("types", getTermAggregation("type", response));
                termsAggList.put("ranges", getRangeAggregation(response));

                return new SearchDtoResponse(total, titles, termsAggList, new ArrayList<>());
            }
        } catch(IOException ex) {
            throw new ElasticUnavailableException(ex);
        }
    }

    private List<Map<String, Object>> getSuggestions(SearchResponse response) {
        Suggest suggest = response.getSuggest();
        PhraseSuggestion termSuggestion = suggest.getSuggestion("spellcheck");
        List<Map<String, Object>> suggestions = new ArrayList<>();
        for (PhraseSuggestion.Entry entry : termSuggestion) {
            for (PhraseSuggestion.Entry.Option option : entry) {
                String suggestText = option.getText().string();
                Float score = option.getScore();
                Map<String, Object> map = new HashMap<>();
                map.put("text", suggestText);
                map.put("score", score);
                suggestions.add(map);
            }
        }
        return suggestions;
    }

    /**
     * Elasticsearch cluster
     * @return the name of the cluster
     * @throws ClusterNotFoundException if the name is not found
     */
    public String getClusterName() throws ClusterNotFoundException {
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
            if (bucket.getDocCount() > 0) {
                dto.put(bucket.getKeyAsString(), bucket.getDocCount());
            }
        }
        return dto;
    }

}
