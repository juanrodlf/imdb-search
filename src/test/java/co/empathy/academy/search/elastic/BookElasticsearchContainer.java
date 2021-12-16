package co.empathy.academy.search.elastic;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class BookElasticsearchContainer extends ElasticsearchContainer {

    private static final String ELASTIC_SEARCH_DOCKER = "elasticsearch:7.11.1";
    private static final String CLUSTER_NAME = "docker_cluster";
    private static final String ELASTIC_SEARCH = "elasticsearch";

    public BookElasticsearchContainer() {
        super(ELASTIC_SEARCH_DOCKER);
        this.addFixedExposedPort(9200, 9200);
        this.addFixedExposedPort(9300, 9300);
        this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
    }

}
