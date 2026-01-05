package com.fitnesscenter.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Appointment;


@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	Appointment findById(int appointment_id);
	
	Page<Appointment> findAll(Pageable pageable);
	
	
}
