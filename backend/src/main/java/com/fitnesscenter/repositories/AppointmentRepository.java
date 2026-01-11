package com.fitnesscenter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Location;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	
}
