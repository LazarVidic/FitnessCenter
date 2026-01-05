package com.fitnesscenter.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Service {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int service_id;
	
	@Column(name="nameService", nullable=false)
	private String nameService;
	
	@Column(name="price", nullable=false)
	private float price;

	
	@ManyToOne
	@JoinColumn(name="appointment_id",nullable=true)
	private Appointment appointment;
	
	
	
	public Service() {
		
	}
	
	public Service(String nameService, float price) {
		super();
		this.nameService = nameService;
		this.price = price;
		
	}
	
	
	public Service(int service_id, String nameService, float price) {
		
		this.service_id = service_id;
		this.nameService = nameService;
		this.price = price;
	}


	
	
	public int getService_id() {
		return service_id;
	}


	public void setService_id(int service_id) {
		this.service_id = service_id;
	}


	public String getNameService() {
		return nameService;
	}


	public void setNameService(String nameService) {
		this.nameService = nameService;
	}


	public float getPrice() {
		return price;
	}


	public void setPrice(float price) {
		this.price = price;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	
	
	
	
}
