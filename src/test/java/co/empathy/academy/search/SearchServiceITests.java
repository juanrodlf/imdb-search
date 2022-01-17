package co.empathy.academy.search;

import co.empathy.academy.search.elastic.DefaultSearchServiceITests;
import co.empathy.academy.search.services.IndexService;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

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

    //Genres tests

    @Test
    public void filterByOneGenreTest() throws Exception {
        String expectedTConst = "tt0964012";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman")
                .param("genre","crime"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Spiderman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    @Test
    public void filterByTwoGenreTest() throws Exception {
        String expectedTConst = "tt0964012";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman")
                        .param("genre","crime,documentary"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Spiderman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }


    @Test
    public void filterByOneNonExistingGenreTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman")
                        .param("genre","nonExisting"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void filterByTwoNonExistingGenreTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman")
                        .param("genre","nonExisting,noExiste"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void filterByOneNonExistingOneExistingGenreTest() throws Exception {
        String expectedTConst = "tt0964012";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "spiderman")
                        .param("genre","crime,nonExisting"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    //Type tests

    @Test
    public void filterByOneTypeTest() throws Exception {
        String expectedTConst = "tt0893456";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "superman")
                        .param("type","short"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Superman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    @Test
    public void filterByTwoTypeTest() throws Exception {
        String expectedTConst = "tt0893456";
        String expectedTConst2 = "tt0893457";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "superman")
                        .param("type","movie,short"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Superman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:2].tConst").value(Matchers.containsInAnyOrder(expectedTConst, expectedTConst2)));
    }


    @Test
    public void filterByOneNonExistingTypeTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "superman")
                        .param("type","nonExisting"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void filterByTwoNonExistingTypeTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "superman")
                        .param("type","nonExisting,noExiste"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items.length()").value(0));
    }

    @Test
    public void filterByOneNonExistingOneExistingTypeTest() throws Exception {
        String expectedTConst = "tt0893456";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "superman")
                        .param("type","short,nonExisting"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    //Range tests

    @Test
    public void filterByOneRangeTest() throws Exception {
        String expectedTConst = "tt1234567";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1940/1947"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].primaryTitle").value("Ironman"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    @Test
    public void filterByTwoRangeTest() throws Exception {
        String expectedTConst = "tt1234567";
        String expectedTConst2 = "tt1234568";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1940/1947,1949/1951"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:2].tConst").value(Matchers.containsInAnyOrder(expectedTConst, expectedTConst2)));
    }


    @Test
    public void filterByOneNonExistingRangeTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1900/1902"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void filterByTwoNonExistingRangeTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1900/1901,1910/1930"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0));
    }

    @Test
    public void filterByOneNonExistingOneExistingRangeTest() throws Exception {
        String expectedTConst = "tt1234567";
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1900/1901,1945/1946"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].tConst").value(expectedTConst));
    }

    @Test
    public void filterByOneRangeThreeFilmsTest() throws Exception {
        String[] expectedTConsts = {"tt1234567", "tt1234568", "tt1234569"};
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman")
                        .param("year","1945/1955"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:3].tConst").value(Matchers.containsInAnyOrder(expectedTConsts)));
    }

    //Aggregations tests

    @Test
    public void aggTest() throws Exception {
        String[] expectedTConsts = {"tt1234567", "tt1234568", "tt1234569"};
        mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "ironman"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$..items[:3].tConst").value(Matchers.containsInAnyOrder(expectedTConsts)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.types.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.types.movie").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.genres.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.genres.Sci-Fi").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.genres.Action").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.ranges.1940-1950").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.ranges.1950-1960").value(2));
    }

    @Test
    public void aggEmptyTest() throws Exception {
        ResultActions actions = mvc.perform(MockMvcRequestBuilders.get("/search").param("query", "queryWithNoResults"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.types.length()").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.genres.length()").value(0));
        for (int i = 1900; i < 2020; i += 10) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$.aggregations.ranges." + i + "-" + (i+10))
                    .value(0));
        }
    }

}
