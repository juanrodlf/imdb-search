package co.empathy.academy.search;

import co.empathy.academy.search.services.SearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchServiceUTests {

    @Mock
    SearchService service;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getQueryTest() {
        String query = "Camisa roja";
        when(service.getQuery(query)).thenReturn(query);
        String queryGet = service.getQuery(query);
        assertEquals(queryGet, query);
    }

    @Test
    public void getClusterNameTest() {
        String clusterName = "elasticsearch";
        when(service.getClusterName()).thenReturn(clusterName);
        String clusterNameGet = service.getClusterName();
        assertEquals(clusterNameGet, clusterName);
    }

}
