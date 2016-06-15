package no.difi.statistics;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    private Client elasticSearchClient;

    @Autowired
    public Statistics(Client elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    public List<TimeSeriesPoint> minutes(String seriesName, Date from, Date to) {
        SearchResponse response = elasticSearchClient.prepareSearch(seriesName)
                .setTypes("minutes")
                .addField("time").addField("value")
                .setQuery(QueryBuilders.rangeQuery("time").from(from).to(to))
                .execute().actionGet();
        List<TimeSeriesPoint> series = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            series.add(point(hit));
        }
        return series;
    }

    private TimeSeriesPoint point(SearchHit hit) {
        return new TimeSeriesPoint(time(hit), value(hit));
    }

    private ZonedDateTime time(SearchHit hit) {
        return ZonedDateTime.parse(hit.field("time").value(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private int value(SearchHit hit) {
        return hit.field("value").value();
    }

}
