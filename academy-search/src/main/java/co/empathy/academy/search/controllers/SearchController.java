package co.empathy.academy.search.controllers;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class SearchController {

    @GetMapping("/search")
    public Map<String, String> search(@RequestParam String query) {
        Map<String, String> map = new HashMap<>();
        map.put("query", query);
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
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
