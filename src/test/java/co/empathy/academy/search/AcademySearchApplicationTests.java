package co.empathy.academy.search;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

	@Test
	void searchReturnsEmptyQueryAndClusterName() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/search?query={query}", ""))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.query").value(""))
				.andExpect(MockMvcResultMatchers.jsonPath("$.clusterName").value("docker-cluster"));
	}

	@Test
	void searchBadRequestWithoutQuery() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/search"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
