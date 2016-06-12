package no.difi.statistics;

import no.difi.statistics.config.AppConfig;
import org.elasticsearch.client.Client;
import org.joda.time.DateTime;
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
public class StatisticsTest {

    @Autowired
    private Client client;
    @Autowired
    private Statistics statistics;

    @Test
    public void test() throws IOException {
        Object response = client.prepareIndex("test", "minutes")
                .setSource(jsonBuilder().startObject()
                        .field("time", Date.from(ZonedDateTime.now().toInstant()))
                        .field("value", 100)
                        .field("desc", "hallo")
                        .endObject()
                ).get();
        List<TimeSeriesPoint> timeSeries = statistics.minutes(
                "test",
                Date.from(ZonedDateTime.now().minusDays(1).toInstant()),
                Date.from(ZonedDateTime.now().toInstant())
        );
        assertEquals(1, timeSeries.size());
    }

}
