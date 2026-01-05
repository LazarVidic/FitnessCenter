package com.fitnesscenter.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Reservation {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)	
	@Column(name="reservation_id")
	private int reservationId;

	
	public Reservation() {
		
	}
	
	public Reservation(int reservationId) {
		super();
		this.reservationId = reservationId;
	}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}
	
	
}
