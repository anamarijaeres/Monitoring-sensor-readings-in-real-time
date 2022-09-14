package hr.fer.tel.rassus.main;

import hr.tel.fer.rassus.producer.KafkaProducerExample;


public class App {
	public static void main(String[] args) {
		runProducer();
		
	}
	private static void runProducer() {
		Thread t=new Thread(new Runnable() {
			public void run() {
				KafkaProducerExample.main(null);
			}
		});
		t.start();
		
	}

}
