package co.empathy.academy.search;

import co.empathy.academy.search.elastic.DefaultSearchServiceITests;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class IndexTests extends DefaultSearchServiceITests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RestHighLevelClient client;

    Logger logger = LoggerFactory.getLogger(IndexTests.class);

    @Test
    void index() throws Exception {
        String dataPath = new File(getClass().getClassLoader().getResource("testdata.tsv").getFile()).getAbsolutePath();
        mvc.perform(MockMvcRequestBuilders.post("/index").param("path", dataPath))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(10000, count());
    }

    private int count() throws IOException {
        Request request = new Request("GET", "/_cat/count/imdb");
        Response response = client.getLowLevelClient().performRequest(request);
        return Integer.parseInt(EntityUtils.toString(response.getEntity()).split(" ")[2].replace("\n", ""));
    }

}
