package hr.fer.tel.rassus.stupidudp.node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import hr.fer.tel.rassus.stupidudp.client.StupidUDPClient;
import hr.fer.tel.rassus.stupidudp.network.EmulatedSystemClock;
import hr.fer.tel.rassus.stupidudp.server.StupidUDPServer;

public class Sensor {
	//all readings from csv file "mjerenja.csv"
	public static List<List<String>> readings;
	
	public static double start;
	private int row;
	
	//every sensor is UDPServer and acts as a multiple UPDClients
	private StupidUDPServer meTheServer;
	private List<StupidUDPClient> allMyNeighbours=new ArrayList<>();
	
	static int index=0;
	//clocks for this Sensor
	public static EmulatedSystemClock myClock=new EmulatedSystemClock();
	public static long scalarClock;
	public static Map<Integer, Integer> vectorClock = new HashMap<>();
	
	//all readings gathered in the time interval of 5 seconds
	public static List<Message> gathered_readings=new ArrayList<>();
	
	public static boolean waitForThePrint=true;
	
	@SuppressWarnings({ "static-access", "static-access" })
	public Sensor() throws IOException, InterruptedException {
		
		int sleepTime=Integer.parseInt(Main.id);
		Thread.sleep(200*sleepTime);
		Sensor.start=System.currentTimeMillis(); //note when the sensorStarted for generateReading()
		//set the scalar and vector clock
		Sensor.scalarClock=myClock.currentTimeMillis();
		vectorClock.put(Integer.parseInt(Main.id), 0);
		for(int i=0;i<Main.otherSensors.size();++i) {
			vectorClock.put(Integer.parseInt(Main.otherSensors.get(i).getId()), 0);
		}
		
		Thread.sleep(1000);
		
		//fill the list readings
		this.readCSV();
		
		//5.3. uspostavljanje UDP komunikacije
		startTheCommunication();
	}
	
	
	private void startTheCommunication() throws InterruptedException {
		
		meTheServer=new StupidUDPServer();
		StupidUDPServer.PORT=Integer.parseInt(Main.port);
		
		//start the UDP server on this sensor
		Thread udpServerThread=new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
					meTheServer.createAndListen();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		udpServerThread.start();
		
		//make UDP clients so that sensor could send his data to all of his neighbors
		for(int i=0; i< Main.otherSensors.size();++i) {
			StupidUDPClient client=new StupidUDPClient();
			client.PORT=Integer.parseInt(Main.otherSensors.get(i).getPort());
			this.allMyNeighbours.add(client);
			
		}
		
		Thread.sleep(3000);
		
		
//		List<String> myReading=this.generateReading();
//		int CO=Integer.parseInt(myReading.get(3));
//		
		
		List<Thread> udpClientThreads=new ArrayList<>();
		
		//start UDP clients on this Sensor
		for(int i=0; i< Main.otherSensors.size();++i) {
			Thread.sleep(500);
			Sensor.index=i;
			//each of these threads is constantly sending current reading to his neighbours and receiving confirmations
			//the result of these needs to go to gathered_readings
			Thread udpClient=new Thread(new Runnable() {
				public void run() {
					StupidUDPClient client=allMyNeighbours.get(Sensor.index);
					try {
						client.sendToMyNeighbour(Main.otherSensors.get(Sensor.index).getAddress());
					} catch (IllegalArgumentException | IOException | InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			});
			udpClient.start();
			
		}
		
		//5.7. sortiranje i racunanje srednje vrijednosti ocitanja
		while(true) {
			waitForThePrint=true;
			if(Main.stopOn) {
				udpServerThread.setDaemon(true);;
				for(Thread t:udpClientThreads) {
					t.setDaemon(true);
				}
			}else {
				Thread.sleep(5000);
			}
			new Thread(new Runnable() {
				public synchronized void  run() {
					List<Message> g_r=new ArrayList<>(Sensor.gathered_readings);
					List<Message> n_r=new ArrayList<>(StupidUDPServer.neighbour_readings);
					for(Message m: n_r) {
						g_r.add(m);
					}
					List<Message> g_r1=new ArrayList<>(g_r);
					Collections.sort(g_r, Comparator.comparing(m -> m.getScalarClock() ));
					System.out.println("--------------------------------------------------------------");
					System.out.println("Sortirani skup po skalarnom:");
					for(Message m: g_r) {
						System.out.println(m);
					}
					
					Collections.sort(g_r1);
					System.out.println("Sortirani skup po vektorskom:");
					
					double sum=0;
					for(Message m: g_r1) {
						System.out.println(m);
						sum+=m.CO;
					}
					
					System.out.println("Prosjecna vrijednost: "+ (sum/g_r1.size()));
					
					Sensor.gathered_readings.clear();
					StupidUDPServer.neighbour_readings.clear();
					System.out.println("--------------------------------------------------------------");
					waitForThePrint=false;
					
				}
			}).start();
			
			Thread.sleep(100);
			
		}
		
		
	}


	private void readCSV() throws IOException {
		  Sensor.readings = new ArrayList<>();
		  try (BufferedReader br = new BufferedReader(new FileReader("mjerenja.csv"))) {
		      String line;
		      while ((line = br.readLine()) != null) {
		    	  if(!line.contains("Temperature")) {
		          String[] values = line.split(",");
		          Sensor.readings.add(Arrays.asList(values));
		    	  }
		      }
		  }
	}
	
//	private List<String> generateReading() {
//		  long seconds= Math.round((System.currentTimeMillis()-start)/1000);
//		  row=(int)((seconds%100)+1);
//		  if(row>100) {
//			  Random r=new Random();
//			  row=(int)(1 + (int)(Math.random() * ((100 - 1) + 1)));
//			  
//		  }
//		  return this.readings.get(row);
//	  }
}
