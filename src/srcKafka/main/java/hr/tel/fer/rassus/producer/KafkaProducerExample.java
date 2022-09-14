package hr.tel.fer.rassus.producer;

import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Scanner;

public class KafkaProducerExample {

    private static String TOPIC = "Command"; //private static String TOPIC = "Register";

    public static void main(String[] args){
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  StringSerializer.class);//CustomSerializer

        Producer<String, String> producer = new KafkaProducer<>(producerProperties); // string Object

        Scanner sc = new Scanner(System.in);
       // 4.1. Pokretanje grupe cvorova slanjem poruke "Start"
       while (true) {
            System.out.println("Write a message to send to consumer on topic " + TOPIC);
            String command = sc.nextLine();
            
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, null, command);//String Object

            producer.send(record);
            producer.flush();
            if(command.equals("Stop")) {
            	//4.2. Zaustavljanje grupe cvorova slanjem poruke "Stop"
            	System.exit(0);
            }
        }
    }
}
