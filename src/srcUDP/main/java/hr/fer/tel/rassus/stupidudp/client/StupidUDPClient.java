/*
 * This code has been developed at Departement of Telecommunications,
 * Faculty of Electrical Eengineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus.stupidudp.client;

import hr.fer.tel.rassus.stupidudp.network.*;
import hr.fer.tel.rassus.stupidudp.node.Ack;
import hr.fer.tel.rassus.stupidudp.node.Main;
import hr.fer.tel.rassus.stupidudp.node.Message;
import hr.fer.tel.rassus.stupidudp.node.Sensor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kresimir Pripuzic <kresimir.pripuzic@fer.hr>
 */
public class StupidUDPClient {
	public boolean received=false;
    public int PORT; //  port of the neighbour
    private int row;
    
    public StupidUDPClient(){
    	
    }
    
    public void sendToMyNeighbour(String sentAddress) throws IllegalArgumentException, IOException, InterruptedException {
    	
    	while(true) {
    		//5.4. Generiranje senzorskih ocitanja
	    	List<String> myReading=this.generateReading();
			int CO=Integer.parseInt(myReading.get(3));
			
			
			
			//povecaj vektorClock za sebe jer si poslao poruku i dohvati scalar clock
			int oldValue=Sensor.vectorClock.get(Integer.parseInt(Main.id));
        	Sensor.vectorClock.replace(Integer.parseInt(Main.id),oldValue+1);
        	Sensor.scalarClock=Sensor.myClock.currentTimeMillis();
        	
        	//5.5. stvaranje UDP paketa s azuriranom vektorskom i skalarnom oznakom
			Message messageToSend=new Message(CO,Sensor.scalarClock,Sensor.vectorClock,Main.id,Main.port);
	    	
			Sensor.gathered_readings.add(messageToSend);
	    	
	    	this.received=false;
	    	byte[] rcvBuf = new byte[1024]; // received bytes
	    	
	    	// determine the IP address of a host, given the host's name
	        InetAddress address = InetAddress.getByName("localhost");
	        
	        // create a datagram socket and bind it to the PORT on the local host
	        //DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
	        DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.3, 1000); //SOCKET
	
	        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
	        ObjectOutputStream os=new ObjectOutputStream(outputStream);
	        os.writeObject(messageToSend);
	        byte[] data= outputStream.toByteArray();
	        DatagramPacket sendPacket=new DatagramPacket(data,data.length,address, this.PORT);
	        socket.send(sendPacket);
	        
	        System.out.println("....................");
	        System.out.println("Message sent with id: "+messageToSend.id+" to receiver: "+this.PORT);
	        System.out.println("....................");
	        while(true) {
	        	Thread.sleep(300);
	        	
	        	DatagramPacket incomingPacket=new DatagramPacket(rcvBuf, rcvBuf.length);
	        	//System.out.println("Length of the packet playing client"+incomingPacket.getLength());
	        	try {
	        		Thread.sleep(100);
		        	socket.receive(incomingPacket);
		        	
		        	byte[] dataResponse=incomingPacket.getData();
		            ByteArrayInputStream in =new ByteArrayInputStream(dataResponse);
		            ObjectInputStream is=new ObjectInputStream(in);
		            try {
		            	
		            	Ack responseAck=(Ack)is.readObject();
		            	System.out.println("++++++++++++++++++++");
			        	System.out.println("Response arrived:"+ responseAck);
			        	System.out.println("++++++++++++++++++++");
			        	
			        	//refresh scalarClock
		            	Sensor.scalarClock=Sensor.myClock.currentTimeMillis();
		            	//scalarClock update
		            	if(responseAck.getScalarClock()>Sensor.scalarClock) {
		            		Sensor.myClock.startTime+=(responseAck.getScalarClock()-Sensor.scalarClock); //update of the clock
		            		Sensor.scalarClock=responseAck.getScalarClock();
		            	}
		      
		            	//vectorClock update
		            	Map<Integer,Integer> messageVetorClock=responseAck.getVectorClock();
		            	for(int i=0;i<Sensor.vectorClock.size();++i) {
		            		if(messageVetorClock.get(i+1)>Sensor.vectorClock.get(i+1)) {
		            			Sensor.vectorClock.replace(i+1,messageVetorClock.get(i+1));
		            		}
		            	}
		              	//povecaj vektorClock za sebe jer si dobio poruku
		            	Sensor.vectorClock.replace(Integer.parseInt(Main.id),Sensor.vectorClock.get(Integer.parseInt(Main.id))+1);
		            }catch (Exception e) {
						e.printStackTrace();
					}
		        	
		        	//if response is a string without scalarClock and vectorClock
//		        	
//		        	String response=new String(incomingPacket.getData());
//		        	System.out.println("++++++++++++++++++++");
//		        	System.out.println("Response arrived:"+ response);
//		        	System.out.println("++++++++++++++++++++");
		            
		        	//povecaj vektorClock za sebe jer si primio poruku ----- nisam ziher da se poveca u slucaju primanja potvrda vec samo kod primanja messagea
		        	//Sensor.vectorClock.replace(Integer.parseInt(Main.id),Sensor.vectorClock.get(Integer.parseInt(Main.id))+1);
		        	this.received=true;
		        	break;
	        	} catch (SocketTimeoutException e) {
	        		//5.6. retransmisija izgubljenih paketa 
	        		if(this.received==false) {
	        			//retransmisija
	        			Thread.sleep(300);
	        			if(!messageToSend.isHasConfirmation()) {
	        				System.out.println(">>>>>>>>>>>> Resending>>>>>>>>>");
	        				System.out.println("Message with id:"+ messageToSend.id+ " resent.");
	        				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		        			DatagramPacket pac = new DatagramPacket(data, data.length, address, this.PORT);
	                        socket.send(pac);
	        			}
                        //povecaj vektorClock za sebe jer si poslao poruku- mislim da se to ne radi za transmisiju
    		        	//Sensor.vectorClock.replace(Integer.parseInt(Main.id),Sensor.vectorClock.get(Integer.parseInt(Main.id))+1);

                        Thread.sleep(100);
                        continue;
	        		}
	        	} catch (IOException ex) {
	        		System.out.println("This exeption.");
	        		Logger.getLogger(StupidUDPClient.class.getName()).log(Level.SEVERE, null, ex);
	        	}
	
	        }
	        Thread.sleep(200);
    	}
        
    }
    
    private List<String> generateReading() {
		  long seconds= Math.round((System.currentTimeMillis()-Sensor.start)/1000);
		  row=(int)((seconds%100)+1);
		  if(row>=100) {
			  Random r=new Random();
			  row=(int)(1 + (int)(Math.random() * ((100 - 1) + 1)));
			  
		  }
		  return Sensor.readings.get(row);
	  }
    
    public void startClient() throws IllegalArgumentException, IOException {
    	String sendString = "Any string...";

        byte[] rcvBuf = new byte[256]; // received bytes

        // encode this String into a sequence of bytes using the platform's
        // default charset and store it into a new byte array

        // determine the IP address of a host, given the host's name
        InetAddress address = InetAddress.getByName("localhost");

        // create a datagram socket and bind it to any available
        // port on the local host
        //DatagramSocket socket = new SimulatedDatagramSocket(0.2, 1, 200, 50); //SOCKET
        DatagramSocket socket = new SimpleSimulatedDatagramSocket(0.2, 200); //SOCKET

        System.out.print("Client sends: ");
        // send each character as a separate datagram packet
        for (int i = 0; i < sendString.length(); i++) {
            byte[] sendBuf = new byte[1];// sent bytes
            sendBuf[0] = (byte) sendString.charAt(i);

            // create a datagram packet for sending data
            DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
                    address, PORT);

            // send a datagram packet from this socket
            socket.send(packet); //SENDTO
            System.out.print(new String(sendBuf));
        }
        System.out.println("");

        StringBuffer receiveString = new StringBuffer();

        while (true) {
            // create a datagram packet for receiving data
            DatagramPacket rcvPacket = new DatagramPacket(rcvBuf, rcvBuf.length);

            try {
                // receive a datagram packet from this socket
                socket.receive(rcvPacket); //RECVFROM
            } catch (SocketTimeoutException e) {
                break;
            } catch (IOException ex) {
                Logger.getLogger(StupidUDPClient.class.getName()).log(Level.SEVERE, null, ex);
            }

            // construct a new String by decoding the specified subarray of bytes
            // using the platform's default charset
            receiveString.append(new String(rcvPacket.getData(), rcvPacket.getOffset(), rcvPacket.getLength()));

        }
        System.out.println("Client received: " + receiveString);

        // close the datagram socket
        socket.close(); //CLOSE
    }
    
    

    public static void main(String args[]) throws IOException {

    }
}
