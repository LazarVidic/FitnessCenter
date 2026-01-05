package com.fitnesscenter.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Appointment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="appointment_id")
	private int appointmentId; 
	
	@Column(nullable=false)
	private LocalDateTime startTime;
	
	@Column(nullable=false)
	private LocalDateTime endTime;
	
	@Column(nullable=false)
	private int maxCapacity;

	
	@ManyToOne
	@JoinColumn(name="reservation_id",nullable=true)
	private Reservation reservation;
	
	
	public Appointment() {
		
	}
	
	public Appointment(LocalDateTime startTime, LocalDateTime endTime, int maxCapacity) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxCapacity = maxCapacity;
	}
	
	public Appointment(int appointmentId, LocalDateTime startTime, LocalDateTime endTime, int maxCapacity) {
		this.appointmentId = appointmentId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxCapacity = maxCapacity;
	}


	public int getAppointmentId() {
		return appointmentId;
	}


	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}


	public LocalDateTime getStartTime() {
		return startTime;
	}


	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}


	public LocalDateTime getEndTime() {
		return endTime;
	}


	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}


	public int getMaxCapacity() {
		return maxCapacity;
	}


	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
	
}
