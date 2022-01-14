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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchDtoResponse getQuery(String searchText, String genres, String types, String ranges) {
        if (searchText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The query cannot be empty.");
        }
        SearchRequest request = new SearchRequest("imdb");

        SearchSourceBuilder requestBuilder = new SearchSourceBuilder();
        requestBuilder.query(buildQuery(searchText, genres, types, ranges));
        addAggregations(requestBuilder);
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

    private void addAggregations(SearchSourceBuilder requestBuilder) {
        requestBuilder.aggregation(AggregationBuilders.terms("genres").field("genres.keyword"));
        requestBuilder.aggregation(AggregationBuilders.terms("type").field("titleType.keyword"));
        RangeAggregationBuilder rangeAgg = AggregationBuilders.range("ranges").field("startYear");
        for (int i = 1900; i < 2020; i += 10) {
            rangeAgg.addRange(i, i + 10);
        }
        requestBuilder.aggregation(rangeAgg);
    }

    private QueryBuilder buildQuery(String searchText, String genres, String types, String ranges) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.matchQuery("primaryTitle", searchText));
        if (genres != null) {
            String[] genresSplit = genres.split(",");
            for (String genre : genresSplit) {
                queryBuilder.must(QueryBuilders.matchQuery("genres", genre));
            }
        }
        if (types != null) {
            String[] typesSplit = types.split(",");
            BoolQueryBuilder typesQuery = QueryBuilders.boolQuery();
            for (String type : typesSplit) {
                typesQuery.should(QueryBuilders.matchQuery("titleType", type));
            }
            queryBuilder.must(typesQuery);
        }
        if (ranges != null) {
            String[] rangesSplit = ranges.split(",");
            BoolQueryBuilder rangeQueries = QueryBuilders.boolQuery();
            for (String range : rangesSplit) {
                String[] years = range.split("/");
                if (years.length != 2) {
                    logger.info("Range \"{}\" not defined properly (Format: YYYY/YYYY)", range);
                    continue;
                }
                int startYear = Integer.parseInt(years[0]);
                int endYear = Integer.parseInt(years[1]);
                rangeQueries.should(QueryBuilders.rangeQuery("startYear").gte(startYear).lte(endYear));
            }
            queryBuilder.must(rangeQueries);
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
