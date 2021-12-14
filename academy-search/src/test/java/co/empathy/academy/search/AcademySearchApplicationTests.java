package co.empathy.academy.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class AcademySearchApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	void searchReturnsQueryAndClusterName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/search?query={query}", "Camisa"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.query").value("Camisa"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.clusterName").value("docker-cluster"));
	}

}
