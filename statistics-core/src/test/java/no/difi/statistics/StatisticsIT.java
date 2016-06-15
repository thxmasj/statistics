package no.difi.statistics;

import no.difi.statistics.config.AppConfig;
import org.elasticsearch.client.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class StatisticsIT {

    @Autowired
    private Client client;
    @Autowired
    private Statistics statistics;

    @Test
    public void givenTimeSeriesWhenQueryingForRangeThenCorrectRangeIsReturned() throws IOException, InterruptedException {
        String index = "test-index";
        String type = "minutes";
        ZonedDateTime now = ZonedDateTime.now();
        indexTimeSeriesPoint(index, type, now.minusMinutes(10), 110);
        indexTimeSeriesPoint(index, type, now.minusMinutes(9), 109);
        indexTimeSeriesPoint(index, type, now.minusMinutes(8), 108);
        indexTimeSeriesPoint(index, type, now.minusMinutes(7), 107);
        Thread.sleep(1000);
        List<TimeSeriesPoint> timeSeries = statistics.minutes(
                index,
                Date.from(now.minusMinutes(9).toInstant()),
                Date.from(now.minusMinutes(8).toInstant())
        );
        assertEquals(2, timeSeries.size());
        assertEquals(109, timeSeries.get(0).value());
        assertEquals(108, timeSeries.get(1).value());
    }

    private void indexTimeSeriesPoint(String index, String type, ZonedDateTime timestamp, int value) throws IOException {
        client.prepareIndex(index, type)
                .setSource(jsonBuilder().startObject()
                        .field("time", Date.from(timestamp.toInstant()))
                        .field("value", value)
                        .endObject()
                ).get();
    }

}
