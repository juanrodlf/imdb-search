package co.empathy.academy.search.services;

import co.empathy.academy.search.model.Title;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.ListUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexService {

    private final ObjectMapper mapper;
    private final RestClient restClient;

    @Autowired
    public IndexService(RestClient restClient) {
        this.mapper = new ObjectMapper();
        this.restClient = restClient;
    }

    public void indexFromTsv(String path) throws IOException {
        Path pathObject = Paths.get(path);
        List<String> lines = Files.readAllLines(pathObject);
        List<String> titlesList = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            titlesList.add(serialize(parseTitle(lines.get(i))));
        }
        List<List<String>> batches = ListUtils.partition(titlesList, 1000);
        batches.parallelStream().forEach(batch -> {
            try {
                String json = batch.parallelStream().collect(Collectors.joining("\n")) + "\n";
                Request req = new Request("POST", "/_bulk");
                req.setJsonEntity(json);
                restClient.performRequest(req);
            }
                catch (IOException e) {
                    e.printStackTrace();
                }

        });
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

    private String serialize(Title title) {
        Index index = new Index("imdb", title.tConst());
        TitleToIndex titleToIndex = new TitleToIndex(title.titleType(),
                title.primaryTitle(),
                title.originalTitle(),
                title.isAdult(),
                title.startYear(),
                title.endYear(),
                title.runtimeMinutes(),
                title.genres());
        try {
            String iSe = mapper.writeValueAsString(index);
            String titleToIndexSe = mapper.writeValueAsString(titleToIndex);
            return "{\"index\":" + iSe + "}\n" + titleToIndexSe;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    record Index(String _index, String _id) {}
    record TitleToIndex(String titleType,
                        String primaryTitle,
                        String originalTitle,
                        Boolean isAdult,
                        Integer startYear,
                        Integer endYear,
                        Integer runtimeMinutes,
                        List<String> genres) {}

}
