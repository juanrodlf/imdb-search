package co.empathy.academy.search.elastic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class DefaultSearchServiceITests {

    @Container
    protected static final ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ImdbElasticsearchContainer();

    @BeforeAll
    static void setUp() {
        ELASTICSEARCH_CONTAINER.start();
    }

    @AfterAll
    static void destroy() {
        ELASTICSEARCH_CONTAINER.stop();
    }

    @Test
    void contextLoads() {

    }

}
