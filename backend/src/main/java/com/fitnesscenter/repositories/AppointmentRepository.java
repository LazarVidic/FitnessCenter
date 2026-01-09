package com.fitnesscenter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    Appointment findById(int appointmentId);
}
