package co.empathy.academy.search.services;

import co.empathy.academy.search.model.Title;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    private final ObjectMapper mapper;
    private final RestHighLevelClient client;

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.mapper = new ObjectMapper();
        this.client = client;
    }

    public void indexFromTsv(String path) throws IOException {
        Path pathObject = Paths.get(path);
        List<String> lines = Files.readAllLines(pathObject);
        List<Title> titlesList = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            titlesList.add(parseTitle(lines.get(i)));
        }
        BulkRequest bulk = new BulkRequest();

        Map<String, Object> jsonMap = new HashMap<>();
        for (Title title : titlesList) {
            jsonMap.clear();
            jsonMap.put("titleType", title.titleType());
            jsonMap.put("primaryTitle", title.primaryTitle());
            jsonMap.put("originalTitle", title.originalTitle());
            jsonMap.put("isAdult", title.isAdult());
            jsonMap.put("startYear", title.startYear());
            jsonMap.put("endYear", title.endYear());
            bulk.add(new IndexRequest("imdb").id(title.tConst()).source(jsonMap));
            client.bulk(bulk, RequestOptions.DEFAULT);
        }
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
