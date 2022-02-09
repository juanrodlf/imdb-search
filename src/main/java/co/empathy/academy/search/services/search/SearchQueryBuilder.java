package co.empathy.academy.search.services.search;

import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.exponentialDecayFunction;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.fieldValueFactorFunction;

public class SearchQueryBuilder {

    private final String searchText, genres, types, ranges;
    private final SearchSourceBuilder requestBuilder;
    Logger logger = LoggerFactory.getLogger(SearchService.class);

    public SearchQueryBuilder(String searchText, String genres, String types, String ranges, SearchSourceBuilder requestBuilder) {
        this.searchText = searchText;
        this.genres = genres;
        this.types = types;
        this.ranges = ranges;
        this.requestBuilder = requestBuilder;
    }

    public QueryBuilder buildQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        FunctionScoreQueryBuilder fsqb = QueryBuilders.functionScoreQuery(queryBuilder, getFunctions());
        queryBuilder.must(QueryBuilders.multiMatchQuery(searchText, "primaryTitle", "originalTitle")
                .tieBreaker(0.3F));
        fsqb.scoreMode(FunctionScoreQuery.ScoreMode.AVG);

        if (genres != null) {
            String[] genresSplit = genres.split(",");
            BoolQueryBuilder genresQuery = QueryBuilders.boolQuery();
            genresQuery.minimumShouldMatch(1);
            for (String genre : genresSplit) {
                genresQuery.should(QueryBuilders.termQuery("genres", genre).caseInsensitive(true));
            }
            queryBuilder.must(genresQuery);
        }
        if (types != null) {
            String[] typesSplit = types.split(",");
            BoolQueryBuilder typesQuery = QueryBuilders.boolQuery();
            for (String type : typesSplit) {
                typesQuery.should(QueryBuilders.termQuery("titleType", type).caseInsensitive(true));
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
        logger.info(fsqb.toString());
        return fsqb;
    }

    private FunctionScoreQueryBuilder.FilterFunctionBuilder[] getFunctions() {
        return new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
            new FunctionScoreQueryBuilder
                    .FilterFunctionBuilder(
                    fieldValueFactorFunction("numVotes")
                            .factor(1.2f)
                            .modifier(FieldValueFactorFunction.Modifier.SQRT)
                            .missing(1.)),
            new FunctionScoreQueryBuilder
                    .FilterFunctionBuilder(
                            fieldValueFactorFunction("averageRating")
                                .factor(1.0f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(1.)),
            new FunctionScoreQueryBuilder
                    .FilterFunctionBuilder(exponentialDecayFunction("startYear", "2022", 1))
        };
    }

    public void addAggregations() {
        requestBuilder.aggregation(AggregationBuilders.terms("genres").field("genres"));
        requestBuilder.aggregation(AggregationBuilders.terms("type").field("titleType"));
        RangeAggregationBuilder rangeAgg = AggregationBuilders.range("ranges").field("startYear");
        for (int i = 1900; i < 2020; i += 10) {
            String key = i + "-" + (i+10);
            rangeAgg.addRange(key, i, i + 10);
        }
        requestBuilder.aggregation(rangeAgg);
    }
}
