package hr.fer.tel.rassus.stupidudp.kafka.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

import hr.fer.tel.rassus.stupidudp.node.Main;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class RegisterConsumer {
	private static String TOPIC = "Register";
	
	
	public void startRegisterConsumer() {
		Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomDeserializer.class);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        Consumer<String, DataSensor> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<String, DataSensor>(consumerProperties);
        consumer.subscribe(Collections.singleton(TOPIC));

        System.out.println("Waiting for messaged to arrive on topic " + TOPIC);

        consumer.poll(Duration.ofMillis(100000));
        consumer.seekToBeginning(consumer.assignment());

        while (true) {
            ConsumerRecords<String, DataSensor> consumerRecords = consumer.poll(Duration.ofMillis(1000));

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
                if(!record.value().getId().equals(Main.id)) {
                	Main.otherSensors.add(record.value());  
                }
            });

            consumer.commitAsync();
            
        }
	}
}
