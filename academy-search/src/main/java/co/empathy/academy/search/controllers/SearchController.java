package co.empathy.academy.search.controllers;


import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private RestHighLevelClient client;

    @GetMapping("/search")
    public Map<String, String> search(@RequestParam String query) {
        Map<String, String> map = new HashMap<>();
        map.put("query", query);
        String clusterName;
        try {
            var request = new ClusterGetSettingsRequest();
            request.includeDefaults(true);
            clusterName = client.cluster().getSettings(request, RequestOptions.DEFAULT)
                    .getSetting("cluster.name");
        }
        catch(IOException e) {
            clusterName = "Cluster name not found";
        }
        map.put("clusterName", clusterName);
        return map;
    }

}
