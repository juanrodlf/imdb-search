package co.empathy.academy.search;

import co.empathy.academy.search.elastic.DefaultSearchServiceITests;
import co.empathy.academy.search.services.IndexService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchServiceITests extends DefaultSearchServiceITests {

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    static void setUp(@Autowired IndexService indexService) throws IOException, InterruptedException {
        ELASTICSEARCH_CONTAINER.start();
        String dataPath = new File(Objects.requireNonNull(SearchServiceITests.class.getClassLoader().getResource("testdatasearch.tsv")).getFile()).getAbsolutePath();
        indexService.indexFromTsv(dataPath);
    }


    @Test
    public void simpleQueryTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "Interstellar"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Interstellar"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value("tt0816692"));
    }

    @Test
    public void accentsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "les miserables"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Les Misérables"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value("tt0084340"));
    }

    @Test
    public void romanNumbersTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "episode 4"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Star Wars: Episode IV - A New Hope"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value("tt0076759"));
    }

    @Test
    public void hyphenTest() throws Exception {
        Matcher<Iterable<? extends String>> matcherTitles = Matchers.containsInAnyOrder("Spider-Man", "Spiderman");
        Matcher<Iterable<? extends String>> matcherIds = Matchers.containsInAnyOrder("tt0145487", "tt0964012");
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:2].primaryTitle").value(matcherTitles))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:2].tConst").value(matcherIds));
    }

    @Test
    public void noResultsTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "noresults"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total").value(0));
    }

    @Test
    public void noQueryTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
