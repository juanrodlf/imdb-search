package co.empathy.academy.search.elastic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class DefaultSearchServiceITests {

    @Container
    private static final ElasticsearchContainer elasticsearchContainer = new ImdbElasticsearchContainer();

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setUp() {
        elasticsearchContainer.start();
    }

    @BeforeEach
    void testContainerRunning() {
        assertTrue(elasticsearchContainer.isRunning());
    }

    @Test
    void searchReturnsQueryAndClusterName() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "Camisa"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.query").value("Camisa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.clusterName").value("docker-cluster"));
    }

    @Test
    void searchBadRequestWithoutQuery() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void searchEmptyQueryBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", ""))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", " 	 "))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @AfterAll
    static void destroy() {
        elasticsearchContainer.stop();
    }


}
