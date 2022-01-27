package co.empathy.academy.search.services.index;

import co.empathy.academy.search.helpers.Util;
import co.empathy.academy.search.services.index.exceptions.IndexAlreadyExistsException;
import co.empathy.academy.search.services.index.exceptions.IndexFailedException;
import co.empathy.academy.search.services.index.exceptions.TitlesFilesNotFoundException;
import org.springframework.http.HttpStatus;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.ResponseException;
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
    private final TabTitleParser parser;

    @Autowired
    public IndexService(RestHighLevelClient client) {
        this.client = client;
        this.parser = new TabTitleParser();
    }

    /**
     * Indexes titles from tsv file. Empty fields are represented with \N
     * @param path Path of the tsv file containing the titles info.
     * @param ratingsPathStr Optional path of the tsv file containing ratings and number of votes.
     * @throws IndexFailedException If the indexing process fails.
     * @throws IOException If there is a problem reading the lines of the file.
     * @throws TitlesFilesNotFoundException If the title file does not exist or is not found.
     * @throws IndexAlreadyExistsException If the index already exists.
     */
    public void indexFromTsv(String path, String ratingsPathStr)
            throws IndexFailedException, IOException, TitlesFilesNotFoundException, IndexAlreadyExistsException {
        createIndex();
        Path pathObject = Paths.get(path);

        if(Files.notExists(pathObject)) {
            throw new TitlesFilesNotFoundException(path);
        }

        List<String> lines = Files.readAllLines(pathObject);

        Map<String, String> ratingMap = getRatingsPath(ratingsPathStr);

        BulkRequest bulk = new BulkRequest();
        bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);

        long start = System.currentTimeMillis();
        for (int i = 1; i < lines.size(); i++) {
            if (i % 100000 == 0) {
                try {
                    client.bulk(bulk, RequestOptions.DEFAULT);
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                    throw new IndexFailedException(ex);
                }
                bulk = new BulkRequest();
                bulk.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            }
            bulk.add(buildRequest(lines.get(i), ratingMap));
        }
        try {
            client.bulk(bulk, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new IndexFailedException(ex);
        }

        logger.info("Bulk process finished in {} seconds", (System.currentTimeMillis() - start) / 1000);
    }

    private Map<String, String> getRatingsPath(String ratingsPathStr) throws IOException {
        Map<String, String> ratingMap = new HashMap<>();
        if (ratingsPathStr != null && !ratingsPathStr.isBlank()) {
            Path ratingsPathObject = Paths.get(ratingsPathStr);
            List<String> ratingLines = Files.readAllLines(ratingsPathObject);
            for (int i = 1; i < ratingLines.size(); i++) {
                String[] splitLine = ratingLines.get(i).split("\t");
                ratingMap.put(splitLine[0], splitLine[1] + "\t" + splitLine[2]);
            }
        }
        return ratingMap;
    }

    private void createIndex() throws IOException, IndexAlreadyExistsException {
        String settings = Util.loadAsString("static/analysis/analyzer.json");
        try {
            Request request = new Request("PUT", "/imdb");
            request.setJsonEntity(settings);
            client.getLowLevelClient().performRequest(request);
        } catch(ResponseException ex) {
            logger.error(ex.getMessage(), ex);
            if (ex.getResponse().getStatusLine().getStatusCode() == HttpStatus.BAD_REQUEST.value()) {
                throw new IndexAlreadyExistsException(ex);
            }
        }
    }

    private IndexRequest buildRequest(String title, Map<String, String> ratingMap) {
        Optional<Map<String, String>> ratings = Optional.empty();
        if (ratingMap != null && ratingMap.size() > 0) {
            ratings = Optional.of(ratingMap);
        }
        Map<String, Object> serialized = parser.parseTitle(title, ratings);
        return new IndexRequest("imdb").id((String) serialized.get("tConst"))
                .source(serialized);
    }

    public void deleteIndex(String index) throws IOException {
        DeleteIndexRequest rq = new DeleteIndexRequest(index);
        client.indices().delete(rq, RequestOptions.DEFAULT);
    }
}
