package co.empathy.academy.search;

import co.empathy.academy.search.elastic.DefaultSearchServiceITests;
import co.empathy.academy.search.services.IndexService;
import co.empathy.academy.search.services.SearchService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchServiceUTests extends DefaultSearchServiceITests {

    @Mock
    SearchService service;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setUp(@Autowired IndexService indexService) throws IOException, InterruptedException {
        elasticsearchContainer.start();
        String dataPath = new File(Objects.requireNonNull(SearchServiceUTests.class.getClassLoader().getResource("testdata.tsv")).getFile()).getAbsolutePath();
        indexService.indexFromTsv(dataPath);
    }


    @Test
    public void getQueryTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "Jukebox"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Jukebox"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value("tt0077440"));
    }



    @Test
    public void getClusterNameTest() {
        String clusterName = "elasticsearch";
        when(service.getClusterName()).thenReturn(clusterName);
        String clusterNameGet = service.getClusterName();
        assertEquals(clusterNameGet, clusterName);
    }

}
