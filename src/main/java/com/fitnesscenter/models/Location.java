package com.fitnesscenter.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Location {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="location_id")
	private int locationId;
	
	@Column(nullable=false)
	private String locationName;
	
	@Column(nullable=false)
	private String locationAdress;

	@ManyToOne
	@JoinColumn(name="appointment_id",nullable=true)
	private Appointment appointment;
	
	@ManyToOne
	@JoinColumn(name="member_id",nullable=true)
	private Member member;
	
	public Location() {
		
	}
	
	public Location( String locationName, String locationAdress) {
		super();
		this.locationName = locationName;
		this.locationAdress = locationAdress;
	}
	
	public Location(int locationId, String locationName, String locationAdress) {
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationAdress = locationAdress;
	}


	public int getLocationId() {
		return locationId;
	}


	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}


	public String getLocationName() {
		return locationName;
	}


	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}


	public String getLocationAdress() {
		return locationAdress;
	}


	public void setLocationAdress(String locationAdress) {
		this.locationAdress = locationAdress;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
	
	
	
	
}
