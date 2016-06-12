package no.difi.statistics.config;

import no.difi.statistics.Statistics;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean(destroyMethod = "close")
    public Client elasticSearchClient() throws UnknownHostException {
        return TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    @Bean
    public Statistics statistics() throws UnknownHostException {
        return new Statistics(elasticSearchClient());
    }

}
