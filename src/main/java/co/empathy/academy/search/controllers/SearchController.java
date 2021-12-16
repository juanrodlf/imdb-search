package co.empathy.academy.search.controllers;


import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import co.empathy.academy.search.responses.SearchDtoResponse;
import co.empathy.academy.search.services.SearchService;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public SearchDtoResponse search(@RequestParam String query) {
        if (query.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The query cannot be empty.");
        }
        String clusterName = searchService.getClusterName();
        return new SearchDtoResponse(query, clusterName);
    }

}
