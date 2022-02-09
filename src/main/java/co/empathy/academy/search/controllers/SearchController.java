package co.empathy.academy.search.controllers;


import co.empathy.academy.search.responses.SearchDtoResponse;
import co.empathy.academy.search.services.search.SearchService;
import co.empathy.academy.search.services.search.exceptions.ElasticUnavailableException;
import co.empathy.academy.search.services.search.exceptions.EmptyQueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public SearchDtoResponse search(@RequestParam String query,
                                    @RequestParam(required = false) String genre,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String year,
                                    @RequestParam(required = false, defaultValue = "0") int start,
                                    @RequestParam(required = false, defaultValue = "10") int rows)
            throws EmptyQueryException, ElasticUnavailableException {
        return searchService.search(query, genre, type, year, start, rows);
    }

}
