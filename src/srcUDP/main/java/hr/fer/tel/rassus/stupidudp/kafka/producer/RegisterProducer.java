package hr.fer.tel.rassus.stupidudp.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import hr.fer.tel.rassus.stupidudp.kafka.consumer.CustomSerializer;
import hr.fer.tel.rassus.stupidudp.kafka.consumer.DataSensor;
import hr.fer.tel.rassus.stupidudp.node.Main;

public class RegisterProducer {
	private static String TOPIC = "Register";
	
	public void startRegisterProducer() {
		 Properties producerProperties = new Properties();
	        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
	        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  CustomSerializer.class);

	        Producer<String, Object> producer = new KafkaProducer<>(producerProperties);

	        //Scanner sc = new Scanner(System.in);
	        String id=Main.id;
	        String port=Main.port;
	        DataSensor data=new DataSensor();
	        data.jsonFormat.put("id", id);
	        data.jsonFormat.put("address", "localhost");
	        data.jsonFormat.put("port", port);
	        data.setAttributes();
	        //while (true) {
	            //System.out.println("Write a message to send to consumer on topic " + TOPIC);
	            //String command = sc.nextLine();

	            ProducerRecord<String, Object> record = new ProducerRecord<>(TOPIC, null, data);

	            producer.send(record);
	            producer.flush();
	       // }
    }
}
