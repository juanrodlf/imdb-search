package co.empathy.academy.search.controllers;

import co.empathy.academy.search.services.IndexService;
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
    public ResponseEntity<?> index(@RequestParam String path) {
        try {
            service.indexFromTsv(path);
            return ResponseEntity.ok().build();
        }
        catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }
        catch (InterruptedException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

}
