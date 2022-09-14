package hr.fer.tel.rassus.stupidudp.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

public class Message implements Serializable,Comparable<Message>{
	private static final long serialVersionUID = 1L;
	public static int idCounter=0;
	public int CO;
	public long scalarClock;
	public Map<Integer, Integer> vectorClock;
	public  int id;
	boolean hasConfirmation=false;
	public String sender;
	public String portOfTheSender;

	
	public Message(int cO, long scalarClock, Map<Integer, Integer> vectorClock, String senderId,String sednerPort) {
		super();
		CO = cO;
		this.scalarClock = Long.valueOf(scalarClock);
		this.vectorClock = new HashMap<>(vectorClock);
		id=Message.idCounter;
		Message.idCounter++;
		this.sender=senderId;
		this.portOfTheSender=sednerPort;
	}

	
	
	public int getCO() {
		return CO;
	}



	public void setCO(int cO) {
		CO = cO;
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

	

	public boolean isHasConfirmation() {
		return hasConfirmation;
	}



	public void setHasConfirmation(boolean hasConfirmation) {
		this.hasConfirmation = hasConfirmation;
	}
	



	@Override
	public String toString() {
		return "Message [CO=" + CO + ", scalarClock=" + scalarClock + ", vectorClock=" + vectorClock + ", id=" + id
				+ "  sender "+sender+ "]";
	}
	
	
	public synchronized int compareTo(Message m){    
		Collection<Integer> valsThis=this.vectorClock.values();
		List<Integer> newValsThis = valsThis.stream().collect(toList());
	
		
		Collection<Integer> valsM=m.vectorClock.values();
		List<Integer> newValsM = valsM.stream().collect(toList());
		
		Integer[] array1=newValsThis.toArray(Integer[]::new);
		Integer[] array2=newValsM.toArray(Integer[]::new);
	
	
		int compValue=0;
		int lastValue=0;
		for(int i=0;i<newValsThis.size();++i) {
			compValue=Arrays.compare(array1, i, newValsThis.size(), array2, i, newValsThis.size());
			if(compValue==0) {
				if(lastValue>0 || lastValue<0) {
					continue;
				}
				lastValue=0;
				return 0;
			}
			if(compValue>0) {
				if(lastValue==0 || lastValue>0) {
					lastValue=compValue;
					continue;
				}else {
					return 0;
				}
			}
			if(compValue<0) {
				if(lastValue==0 || lastValue<0) {
					lastValue=compValue;
					continue;
				}else {
					return 0;
				}
			}
		}
		if(lastValue<0) {
			return -1;
		}
		if(lastValue>0) {
			return 1;
		}
		return 0;
	}
	



	
	
	
	
}
