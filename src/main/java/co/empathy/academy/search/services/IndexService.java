package co.empathy.academy.search.services;

import co.empathy.academy.search.helpers.Util;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class IndexService {

    private final RestHighLevelClient client;
    Logger logger = LoggerFactory.getLogger(IndexService.class);

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    public void indexFromTsv(String path, String ratingsPathStr) throws IOException, InterruptedException {
        createIndex();
        Path pathObject = Paths.get(path);
        List<String> lines = Files.readAllLines(pathObject);

        Map<String, String> ratingMap = new HashMap<>();
        if (ratingsPathStr != null && !ratingsPathStr.isBlank()) {
            Path ratingsPathObject = Paths.get(ratingsPathStr);
            List<String> ratingLines = Files.readAllLines(ratingsPathObject);
            for (int i = 1; i < ratingLines.size(); i++) {
                String[] splitLine = ratingLines.get(i).split("\t");
                ratingMap.put(splitLine[0], splitLine[1] + "\t" + splitLine[2]);
            }
        }

        BulkRequest bulk = new BulkRequest();
        bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        long start = System.currentTimeMillis();
        for (int i = 1; i < lines.size(); i++) {
            if (i % 100000 == 0) {
                client.bulk(bulk, RequestOptions.DEFAULT);
                bulk = new BulkRequest();
                bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            }
            bulk.add(buildRequest(lines.get(i), ratingMap));
        }
        client.bulk(bulk, RequestOptions.DEFAULT);

        logger.info("Bulk process finished in {} seconds", (System.currentTimeMillis() - start) / 1000);
    }

    private void createIndex() {
        String settings = Util.loadAsString("static/analysis/analyzer.json");
        try {
            Request request = new Request("PUT", "/imdb");
            request.setJsonEntity(settings);
            client.getLowLevelClient().performRequest(request);
        } catch(IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    private IndexRequest buildRequest(String title, Map<String, String> ratingMap) {
        Optional<Map<String, String>> ratings = Optional.empty();
        if (ratingMap != null && ratingMap.size() > 0) {
            ratings = Optional.of(ratingMap);
        }
        Map<String, Object> serialized = parseTitle(title, ratings);
        return new IndexRequest("imdb").id((String) serialized.get("tConst"))
                .source(serialized);
    }

    private Map<String, Object> parseTitle(String line, Optional<Map<String, String>> ratings) {
        Map<String, Object> map = new HashMap<>();
        String[] values = line.split("\t");
        map.put("tConst", validateStr(values[0]));
        map.put("titleType", validateStr(values[1]));
        map.put("primaryTitle", validateStr(values[2]));
        map.put("originalTitle", validateStr(values[3]));
        map.put("isAdult", validateBool(values[4]));
        map.put("startYear", validateInt(values[5]));
        map.put("endYear", validateInt(values[6]));
        map.put("runtimeMinutes", validateInt(values[7]));
        map.put("genres", genresToList(values[8]));
        if (ratings.isPresent()) {
            Map<String, String> ratingsMap = ratings.get();
            String value = ratingsMap.get(validateStr(values[0]));
            if (value != null) {
                String[] ratingValues = value.split("\t");
                map.put("averageRating", validateFloat(ratingValues[0]));
                map.put("numVotes", validateInt(ratingValues[1]));
            }
            else {
                map.put("averageRating", null);
                map.put("numVotes", null);
            }
        }
        return map;
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

    private Float validateFloat(String str) {
        return str.equals("\\N") ? null : Float.parseFloat(str);
    }

    private void addRatings(String path) throws IOException {
        Path ratingsPath = Paths.get(path);
        List<String> lines = Files.readAllLines(ratingsPath);

        BulkRequest bulk = new BulkRequest();
        bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        for (int i = 1; i < lines.size(); i++) {
            if (i % 100000 == 0) {
                client.bulk(bulk, RequestOptions.DEFAULT);
                bulk = new BulkRequest();
                bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            }
            String[] splitLine = lines.get(i).split("\t");
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("averageRating", splitLine[1]);
            jsonMap.put("numVotes", splitLine[2]);
            UpdateRequest request = new UpdateRequest("imdb", splitLine[0])
                    .doc(jsonMap);
            bulk.add(request);
        }
        client.bulk(bulk, RequestOptions.DEFAULT);
    }

    public void deleteIndex(String index) throws IOException {
        DeleteIndexRequest rq = new DeleteIndexRequest(index);
        client.indices().delete(rq, RequestOptions.DEFAULT);
    }
}
