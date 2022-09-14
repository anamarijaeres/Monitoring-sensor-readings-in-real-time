package hr.fer.tel.rassus.stupidudp.kafka.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import hr.fer.tel.rassus.stupidudp.node.Main;
public class StartConsumer {
	private static String TOPIC = "Command";
	private static boolean startOn=false;
	
	
	public void startStartConsumer() {
		
		 Properties consumerProperties = new Properties();
	        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
	        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
	        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
	        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	        Consumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(consumerProperties);
	        consumer.subscribe(Collections.singleton(TOPIC));

	        System.out.println("Waiting for messaged to arrive on topic " + TOPIC);

	        consumer.poll(Duration.ofMillis(1000));
	        consumer.seekToBeginning(consumer.assignment());
	       
	        while (true) {
	            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));

	            consumerRecords.forEach(record -> {
	                System.out.printf("Consumer Record:(%d, %s, %d, %d)\n",
	                        record.key(), record.value(),
	                        record.partition(), record.offset());
	            });

	            consumer.commitAsync();
	            if(consumerRecords.count()!=0) {
	            	 consumerRecords.forEach(record -> {
	 	               if(record.value().equals("Start")) {
	 	            	   Main.startOn=true;
	 	               }else if(record.value().equals("Stop")) {
	 	            	   Main.stopOn=true;
	 	               }
	 	            });
	            }
	       }
	}
}
