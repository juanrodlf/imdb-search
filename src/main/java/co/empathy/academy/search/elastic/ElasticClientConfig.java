package co.empathy.academy.search.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticClientConfig {

    @Value("${elastic.host}")
    private String host;

    @Value("${elastic.port}")
    private int port;

    @Value("${elastic.scheme}")
    private String scheme;

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(clientBuilder());
    }

    @Bean
    public RestClientBuilder clientBuilder() {
        return RestClient.builder( new HttpHost(host, port, scheme) );
    }

}
