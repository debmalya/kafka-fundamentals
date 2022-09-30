
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Nullable;


public class ClickEventSerializationSchema implements KafkaSerializationSchema<ClickEvent> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String topic;

    public ClickEventSerializationSchema(){
    }

    public ClickEventSerializationSchema(String topic) {
        this.topic = topic;
    }

    @Override
    public ProducerRecord<byte[], byte[]> serialize(
            final ClickEvent message, @Nullable final Long timestamp) {
        try {
            //if topic is null, default topic will be used
            return new ProducerRecord<>(topic, objectMapper.writeValueAsBytes(message));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not serialize record: " + message, e);
        }
    }
}
