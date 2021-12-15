package co.empathy.academy.search.services;

import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import org.elasticsearch.action.admin.cluster.settings.ClusterGetSettingsRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient client;

    public String getClusterName() {
        String clusterName;
        try {
            var request = new ClusterGetSettingsRequest();
            request.includeDefaults(true);
            clusterName = client.cluster().getSettings(request, RequestOptions.DEFAULT)
                    .getSetting("cluster.name");
        }
        catch(IOException e) {
            throw new ClusterNotFoundException(e);
        }
        return clusterName;
    }

}
