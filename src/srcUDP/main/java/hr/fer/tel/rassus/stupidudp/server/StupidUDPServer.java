/*
 * This code has been developed at Departement of Telecommunications,
 * Faculty of Electrical Engineering and Computing, University of Zagreb.
 */
package hr.fer.tel.rassus.stupidudp.server;

import hr.fer.tel.rassus.stupidudp.network.SimpleSimulatedDatagramSocket;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.protocol.MessageSizeAccumulator;

/**
 *
 * @author Kresimir Pripuzic <kresimir.pripuzic@fer.hr>
 */
public class StupidUDPServer {

    public static int PORT; // server port of the Sensor
    
    public static List<Message> neighbour_readings=new ArrayList<>(); //list for all readings from neighbours
    
    public StupidUDPServer() {
    	
    }
    
    public void createAndListen() throws IOException, InterruptedException {
    	
    	// create a UDP socket and bind it to the specified port on the localhost
        DatagramSocket socket = new SimpleSimulatedDatagramSocket(PORT, 0.3, 1000); //SOCKET -> BIND
    	
        byte[] rcvBuf = new byte[1024]; // received bytes
        byte[] sendBuf = new byte[1024];// sent bytes
        
        while(true) {
        	Thread.sleep(200);
        	// create a DatagramPacket for receiving packets
            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);
            
            // receive packet
            socket.receive(packet); //RECVFROM
            
            byte[] data=packet.getData();
            ByteArrayInputStream in =new ByteArrayInputStream(data);
            ObjectInputStream is=new ObjectInputStream(in);
            try {
            	
            	Message message=(Message)is.readObject();
            	System.out.println();
            	System.out.println("***************");
            	System.out.println("Message object received ="+ message);
            	System.out.println();
            	System.out.println("***************");
            	
            	boolean alreadyHaveIt=false;
            	for(Message m:StupidUDPServer.neighbour_readings) {
            		if(m.id==message.id && m.sender.equals(message.sender) && m.portOfTheSender.equals(message.portOfTheSender)) {alreadyHaveIt=true;}
            	}
            	
            	
            	message.setHasConfirmation(true);
            	
            	//refresh scalarClock
            	Sensor.scalarClock=Sensor.myClock.currentTimeMillis();
            	//scalarClock update
            	if(message.getScalarClock()>Sensor.scalarClock) {
            		Sensor.myClock.startTime+=(message.getScalarClock()-Sensor.scalarClock); //update of the clock
            		Sensor.scalarClock=message.getScalarClock();
            	}
      
            	//vectorClock update
            	Map<Integer,Integer> messageVetorClock=message.getVectorClock();
            	for(int i=0;i<Sensor.vectorClock.size();++i) {
            		if(messageVetorClock.get(i+1)>Sensor.vectorClock.get(i+1)) {
            			Sensor.vectorClock.replace(i+1,messageVetorClock.get(i+1));
            		}
            	}
              	//povecaj vektorClock za sebe jer si dobio poruku
            	Sensor.vectorClock.replace(Integer.parseInt(Main.id),Sensor.vectorClock.get(Integer.parseInt(Main.id))+1);
            	
            	if(!alreadyHaveIt) {
        			StupidUDPServer.neighbour_readings.add(message);
        			
        		}
            	
            
            //preparing the reply
            InetAddress IPAddress=packet.getAddress();
            int port= packet.getPort();
            
            //povecaj jer vektorClock za sebe jer si poslao poruku --- mislim da se replyevi ne broje
            Sensor.vectorClock.replace(Integer.parseInt(Main.id),Sensor.vectorClock.get(Integer.parseInt(Main.id))+1);
            Ack reply=new Ack(Sensor.scalarClock,Sensor.vectorClock,message.id,StupidUDPServer.PORT,port);
            
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
	        ObjectOutputStream os=new ObjectOutputStream(outputStream);
	        os.writeObject(reply);
	        byte[] dataReply= outputStream.toByteArray();
	        DatagramPacket replyPacket=new DatagramPacket(dataReply,dataReply.length,IPAddress, port);
	        socket.send(replyPacket);
            
	        //if reply can be a String without vectorClock and scalarClock
//            
//            String reply=" for message: " +message.id+ " from  "+ PORT ;
//            byte[] replyBytea=reply.getBytes();
//            DatagramPacket replyPacket= new DatagramPacket(replyBytea, replyBytea.length, IPAddress, port);
//            socket.send(replyPacket);
            
            
        	
            Thread.sleep(100);
            }catch (Exception e) {
				e.printStackTrace();
			}
        
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException {

//        byte[] rcvBuf = new byte[1024]; // received bytes
//        byte[] sendBuf = new byte[1024];// sent bytes
//        String rcvStr;
//
//        // create a UDP socket and bind it to the specified port on the local
//        // host
//        DatagramSocket socket = new SimpleSimulatedDatagramSocket(PORT, 0.2, 200); //SOCKET -> BIND
//
//        while (true) { //OBRADA ZAHTJEVA
//            // create a DatagramPacket for receiving packets
//            DatagramPacket packet = new DatagramPacket(rcvBuf, rcvBuf.length);
//
//            // receive packet
//            socket.receive(packet); //RECVFROM
//
//            // construct a new String by decoding the specified subarray of
//            // bytes
//            // using the platform's default charset
//            rcvStr = new String(packet.getData(), packet.getOffset(),
//                    packet.getLength());
//            System.out.println("Server received: " + rcvStr);
//
//            // encode a String into a sequence of bytes using the platform's
//            // default charset
//            sendBuf = rcvStr.toUpperCase().getBytes();
//            System.out.println("Server sends: " + rcvStr.toUpperCase());
//
//            // create a DatagramPacket for sending packets
//            DatagramPacket sendPacket = new DatagramPacket(sendBuf,
//                    sendBuf.length, packet.getAddress(), packet.getPort());
//
//            // send packet
//            socket.send(sendPacket); //SENDTO
//        }
        
    }
}
