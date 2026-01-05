package com.fitnesscenter.dtos;

public class LocationDto {

	private String locationName;
	
	private String locationAdress;

	
	private int member_id;
	
	private int appointment_id;
	
	
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

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public int getAppointment_id() {
		return appointment_id;
	}

	public void setAppointment_id(int appointment_id) {
		this.appointment_id = appointment_id;
	}
	

	
	
	
}
