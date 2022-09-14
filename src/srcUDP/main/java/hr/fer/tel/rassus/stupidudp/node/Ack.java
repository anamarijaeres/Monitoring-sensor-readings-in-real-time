package hr.fer.tel.rassus.stupidudp.node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Ack implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public long scalarClock;
	public Map<Integer, Integer> vectorClock;
	public  int idForMessage;
	public int portOfTheSender;
	public int portOfTheReceiver;
	
	
	public Ack(long scalarClock, Map<Integer, Integer> vectorClock, int idForMessage, int portOfTheSender,
			int portOfTheReceiver) {
		super();
		this.scalarClock = Long.valueOf(scalarClock);
		this.vectorClock = new HashMap<>(vectorClock);
		this.idForMessage = idForMessage;
		this.portOfTheSender = portOfTheSender;
		this.portOfTheReceiver = portOfTheReceiver;
	}


	public long getScalarClock() {
		return scalarClock;
	}


	public void setScalarClock(long scalarClock) {
		this.scalarClock = scalarClock;
	}


	public Map<Integer, Integer> getVectorClock() {
		return vectorClock;
	}


	public void setVectorClock(Map<Integer, Integer> vectorClock) {
		this.vectorClock = vectorClock;
	}


	public int getIdForMessage() {
		return idForMessage;
	}


	public void setIdForMessage(int idForMessage) {
		this.idForMessage = idForMessage;
	}


	public int getPortOfTheSender() {
		return portOfTheSender;
	}


	public void setPortOfTheSender(int portOfTheSender) {
		this.portOfTheSender = portOfTheSender;
	}


	public int getPortOfTheReceiver() {
		return portOfTheReceiver;
	}


	public void setPortOfTheReceiver(int portOfTheReceiver) {
		this.portOfTheReceiver = portOfTheReceiver;
	}


	@Override
	public String toString() {
		return "Ack [idForMessage=" + idForMessage +", scalarClock=" + scalarClock + ", vectorClock=" + vectorClock 
				+ ", portOfTheSender=" + portOfTheSender+" ]";
	}
	
	
	
	

}
