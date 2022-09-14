package hr.fer.tel.rassus.stupidudp.kafka.consumer;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataSensor {
	
	String id;

	String address;
	
	String port;
	
	public Map<String,String> jsonFormat=new HashMap<>();
	
	public DataSensor() {
		
	}
	public DataSensor(String id, String address, String port) {
		super();
		this.id = id;
		this.address = address;
		this.port = port;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public void setAttributes() {
		this.id=this.jsonFormat.get("id");
		this.address=this.jsonFormat.get("address");
		this.port=this.jsonFormat.get("port");
	}
	@Override
	public String toString() {
		return "DataSensor [id=" + id + ", address=" + address + ", port=" + port + "]";
	}
	
	

}
