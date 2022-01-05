package co.empathy.academy.search.services;

import co.empathy.academy.search.helpers.Util;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.recycler.Recycler;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    private final RestHighLevelClient client;
    Logger logger = LoggerFactory.getLogger(IndexService.class);
    private ObjectMapper mapper;

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
    }

    public void indexFromTsv(String path) throws IOException, InterruptedException {
        createIndex();
        Path pathObject = Paths.get(path);
        List<String> lines = Files.readAllLines(pathObject);

        BulkRequest bulk = new BulkRequest();
        long start = System.currentTimeMillis();

        for (int i = 1; i < lines.size(); i++) {
            if (i % 100000 == 0) {
                client.bulk(bulk, RequestOptions.DEFAULT);
                bulk = new BulkRequest();
            }
            bulk.add(buildRequest(lines.get(i)));
        }
        client.bulk(bulk, RequestOptions.DEFAULT);
        logger.info("Bulk process finished in {} seconds", (System.currentTimeMillis() - start) / 1000);
    }

    private void createIndex() {
        String settings = Util.loadAsString("static/es-settings.json");
        String mapping = Util.loadAsString("static/mappings/title.json");
        try {
            if (settings == null || mapping == null) {
                logger.error("Failed to create index.");
                return;
            }
            CreateIndexRequest createIndexRequest = new CreateIndexRequest("title");
            createIndexRequest.settings(settings,XContentType.JSON);
            createIndexRequest.mapping(mapping, XContentType.JSON);

            client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch(IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private IndexRequest buildRequest(String title) {
        Map<String, Object> serialized = parseTitle(title);
        return new IndexRequest("imdb").id((String) serialized.get("tConst"))
                .source(serialized);
    }

    private Map<String, Object> parseTitle(String line) {
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

}
