package hr.fer.tel.rassus.stupidudp.kafka.consumer;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomDeserializer implements Deserializer<DataSensor> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public DataSensor deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        DataSensor object = null;
        try {
        object = mapper.readValue(data, DataSensor.class);
        } catch (Exception exception) {
        	System.out.println("Error in deserializing bytes "+ exception);
        }
        return object;
    }

    @Override
    public void close() {
    }

}