package hr.fer.tel.rassus.stupidudp.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hr.fer.tel.rassus.stupidudp.client.StupidUDPClient;
import hr.fer.tel.rassus.stupidudp.server.StupidUDPServer;
import hr.fer.tel.rassus.stupidudp.kafka.consumer.DataSensor;
import hr.fer.tel.rassus.stupidudp.kafka.consumer.RegisterConsumer;
import  hr.fer.tel.rassus.stupidudp.kafka.consumer.StartConsumer;
import hr.fer.tel.rassus.stupidudp.kafka.producer.RegisterProducer;
public class Main {

	public static boolean startOn=false;
	public static boolean stopOn=false;
	public static List<DataSensor> otherSensors=new ArrayList<>();
	public static String id="";
	public static String port="";
	
	public static void main(String[] args) throws InterruptedException {
		//5.1. Inicijalizacija cvora -- upis id i porta
		Scanner sc=new Scanner(System.in);
		System.out.println("Id of the sensor please:");
		Main.id=sc.nextLine();
		System.out.println("Port of the sensor please:");
		Main.port=sc.nextLine();
		
		//not used
//		StupidUDPServer server=new StupidUDPServer();
//		StupidUDPClient client=new StupidUDPClient();
//		int port=10000;
		
		//consumer for the topic "Command"
		Thread startConsumerThread= new Thread(new Runnable() {
			public void run() {
				StartConsumer startConsumer=new StartConsumer();
				startConsumer.startStartConsumer();
				
			}
		});
		startConsumerThread.start();
		
		//consumer for the topic "Register"
		Thread registerConsumerThread=new Thread(new Runnable() {
			public void run() {
				RegisterConsumer registerConsumer=new RegisterConsumer();
				registerConsumer.startRegisterConsumer();
			}
		});
		registerConsumerThread.start();
		
		//Waiting for the message Start
		while(Main.startOn==false) {
			Thread.sleep(1000);
		}
		
		//5.2. Registracija cvora slanjem poruke na temu "Register" u json formatu
		//producer for the topic "Register" 
		Thread registerProducerThread=new Thread(new Runnable() {
			public void run() {	
				RegisterProducer registerProducer=new RegisterProducer();
				registerProducer.startRegisterProducer();
			}
		});
		
		registerProducerThread.start();
		
		
		Thread.sleep(1000);
		
		while(Main.otherSensors.size()==0) {
			Thread.sleep(1000);
		}
		
		//5.3. Uspostavljanje UDP komunikacije pokretanjem klase Sensor
		new Thread(new Runnable() {
			public void run() {
				System.out.println("My neighbours:");
				//print all registered neighbours
				for(DataSensor ds: Main.otherSensors) {
					System.out.println(ds.toString());
				}
				try {
					Sensor me=new Sensor();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		//5.8. Zaustavljanje rada cvora
		//When message "Stop" on topic "Command" is produced terminate all processes 
		while(Main.stopOn==false || Sensor.waitForThePrint) {
			Thread.sleep(10);
		}
		
		System.exit(0);
		
	}

}
