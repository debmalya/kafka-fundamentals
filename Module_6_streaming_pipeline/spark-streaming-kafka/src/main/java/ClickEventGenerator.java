import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class ClickEventGenerator {
    static final int EVENTS_PER_WINDOW = 1000;
    static final List<String> pages = Arrays.asList("/help", "/index", "/shop", "/jobs", "/about", "/news");
    static String topic = "input-click";
    public static final Time WINDOW_SIZE = Time.of(15, TimeUnit.SECONDS);

    public static void main(String[] args) throws Exception {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());


        KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(kafkaProps);

        ClickIterator clickIterator = new ClickIterator();

        while (true) {

            ProducerRecord<byte[], byte[]> record = new ClickEventSerializationSchema(topic).serialize(
                    clickIterator.next(),
                    null);

            producer.send(record);

            Thread.sleep(2);
        }
    }


    static class ClickIterator  {

        private static final int NUMBER_OF_USERS = 5;
        private static final int MAX_DURATION = 200;
        private Map<String, Long> nextTimestampPerKey;
        private int nextPageIndex;

        ClickIterator() {
            nextTimestampPerKey = new HashMap<>();
            nextPageIndex = 0;
        }

        ClickEvent next() {
            String page = nextPage();
            long userId = nextUserId();
            long duration = nextDuration();
            return new ClickEvent(nextTimestamp(page), page, userId, duration);
        }

        private Date nextTimestamp(String page) {
            long nextTimestamp = nextTimestampPerKey.getOrDefault(page, 0L);
            nextTimestampPerKey.put(page, nextTimestamp + WINDOW_SIZE.toMilliseconds() / EVENTS_PER_WINDOW);
            return new Date(nextTimestamp);
        }

        private String nextPage() {
            String nextPage = pages.get(nextPageIndex);
            if (nextPageIndex == pages.size() - 1) {
                nextPageIndex = 0;
            } else {
                nextPageIndex++;
            }
            return nextPage;
        }

        private long nextUserId() {
            Random rnd = new Random();
            return 2013000000 + rnd.nextInt(NUMBER_OF_USERS);

        }

        private long nextDuration() {
            Random rnd = new Random();
            return rnd.nextInt(MAX_DURATION);

        }

    }


}
