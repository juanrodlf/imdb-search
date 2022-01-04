package co.empathy.academy.search.services;

import co.empathy.academy.search.model.Title;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class IndexService {

    private final RestHighLevelClient client;
    Logger logger = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    public void indexFromTsv(String path) throws IOException, InterruptedException {
        Path pathObject = Paths.get(path);
        Stream<String> lines = Files.lines(pathObject);

        BulkRequest bulk = new BulkRequest();
        lines.skip(1).forEach(line ->
            bulk.add(buildRequest(parseTitle(line)))
        );
        client.bulk(bulk, RequestOptions.DEFAULT);
    }

    private IndexRequest buildRequest(Title title) {
        String serialized = Title.getAsString(title);
        return new IndexRequest("imdb")
                                            .id(title.tConst())
                                            .source(serialized, XContentType.JSON);
    }

    private Title parseTitle(String line) {
        String[] values = line.split("\t");
        String tConst = validateStr(values[0]);
        String titleType = validateStr(values[1]);
        String primaryTitle = validateStr(values[2]);
        String originalTitle = validateStr(values[3]);
        Boolean isAdult = validateBool(values[4]);
        Integer startYear = validateInt(values[5]);
        Integer endYear = validateInt(values[6]);
        Integer runtimeMinutes = validateInt(values[7]);
        List<String> genres = genresToList(values[8]);
        return new Title(tConst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres);
    }

    private String validateStr(String str) {
        return str.equals("\\N") ? null : str;
    }

    private Boolean validateBool(String str) {
        return str.equals("\\N") ? null : str.equals("1");
    }

    private Integer validateInt(String str) {
        return str.equals("\\N") ? null : Integer.parseInt(str);
    }

    private List<String> genresToList (String str) {
        return str.equals("\\N") ? List.of() : List.of(str.split(","));
    }

}
