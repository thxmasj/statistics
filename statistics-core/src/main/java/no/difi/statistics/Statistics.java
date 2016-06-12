package no.difi.statistics;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistics {

    private Client client;

    @Autowired
    public Statistics(Client client) {
        this.client = client;
    }

    public List<TimeSeriesPoint> minutes(String seriesName, Date from, Date to) {
        SearchResponse response = client.prepareSearch(seriesName)
                .setTypes("minutes")
                .setQuery(QueryBuilders.rangeQuery("time").from(from).to(to))
                .execute().actionGet();
        List<TimeSeriesPoint> series = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            series.add(point(hit));
        }
        return series;
    }

    private TimeSeriesPoint point(SearchHit hit) {
        return new TimeSeriesPoint(hit.field("time").value(), hit.field("value").value());
    }

}
