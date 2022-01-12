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
    protected static final ElasticsearchContainer elasticsearchContainer = new ImdbElasticsearchContainer();

    @BeforeAll
    static void setUp() {
        elasticsearchContainer.start();
    }

    @BeforeEach
    void testContainerRunning() {
        assertTrue(elasticsearchContainer.isRunning());
    }

    @AfterAll
    static void destroy() {
        elasticsearchContainer.stop();
    }

    @Test
    void contextLoads() {

    }

}
