package co.empathy.academy.search.controllers;

import co.empathy.academy.search.services.index.IndexService;
import co.empathy.academy.search.services.index.exceptions.IndexAlreadyExistsException;
import co.empathy.academy.search.services.index.exceptions.IndexFailedException;
import co.empathy.academy.search.services.index.exceptions.TitlesFilesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class IndexController {

    @Autowired
    private IndexService service;

    @PostMapping("/index")
    public ResponseEntity<?> index(@RequestParam String path,
                                   @RequestParam(required = false) String ratingsPath)
            throws TitlesFilesNotFoundException, IndexFailedException, IndexAlreadyExistsException {
        try {
            service.indexFromTsv(path, ratingsPath);
            return ResponseEntity.ok().build();
        }
        catch (IOException ex) {
            return ResponseEntity.badRequest().body(ex.toString());
        }
    }

    @PostMapping("/index/delete")
    public ResponseEntity<?> deleteIndex(@RequestParam String index) {
        try {
            service.deleteIndex(index);
            return ResponseEntity.ok().build();
        }
        catch(Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
