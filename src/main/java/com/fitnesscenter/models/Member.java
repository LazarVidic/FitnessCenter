package com.fitnesscenter.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Member {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="member_id")
	private int memberId;
	
	@Column(name="memberName",nullable=false)
	private String memberName;
	
	@Column(name="memberSurname",nullable=false)
	private String memberSurname;
	
	@Column(name="phone",nullable=false)
	private String phone;
	
	@Enumerated(EnumType.STRING)
	@Column(name="roll",nullable=false)
	private Roll roll;
	
	@Column(name="email",nullable=false,unique=true)
	private String email;
	
	@Column(name="password",nullable=false)
	private String password;
	
	@Column(name="username",nullable=false)
	private String username;

	
	@ManyToOne
	@JoinColumn(name="reservation_id",nullable=true)
	private Reservation reservation;
	
	public Member() {
		
	}
	
	public Member(String memberName, String memberSurname, String phone, Roll roll, String email, String password,
			String username) {
		
		this.memberName = memberName;
		this.memberSurname = memberSurname;
		this.phone = phone;
		this.roll = roll;
		this.email = email;
		this.password = password;
		this.username = username;
		
		
	}
	
	public Member(int memberId, String memberName, String memberSurname, String phone, Roll roll, String email, String password,
			String username) {
	
		this.memberId = memberId;
		this.memberName = memberName;
		this.memberSurname = memberSurname;
		this.phone = phone;
		this.roll = roll;
		this.email = email;
		this.password = password;
		this.username = username;
	}


	public int getMemberId() {
		return memberId;
	}


	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}


	public String getMemberName() {
		return memberName;
	}


	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberSurname() {
		return memberSurname;
	}


	public void setMemberSurname(String userSurname) {
		this.memberSurname = userSurname;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public Roll getRoll() {
		return roll;
	}


	public void setRoll(Roll roll) {
		this.roll = roll;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}
	
	
	public Reservation getReservation() {
		return reservation;
	}


	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
	
	
}
