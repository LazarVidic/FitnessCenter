package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitnesscenter.dtos.AppointmentDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.repositories.AppointmentRepository;

@Service
public class AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	public List<Appointment> getAllAppointments(){
		return appointmentRepository.findAll();
	}
	
	public Appointment getAppointmentById(int appointment_id) {
		Appointment appointment = appointmentRepository.findById(appointment_id);
		
		if(appointment==null) {
			return null;
		}
		
		return appointment;
	}
	
	public Appointment createAppointment(AppointmentDto appointmentDto) {
		
		Appointment appointment = new Appointment(appointmentDto.getStartTime(),
				appointmentDto.getEndTime(), appointmentDto.getMaxCapacity() );
		
		return appointmentRepository.save(appointment);
	}
	
	public Appointment updateAppointment(int appointment_id, AppointmentDto appointmentDto) {
		Appointment appointment = appointmentRepository.findById(appointment_id);
		
		appointment.setStartTime(appointmentDto.getStartTime());
		appointment.setEndTime(appointmentDto.getEndTime());
		appointment.setMaxCapacity(appointmentDto.getMaxCapacity());
		
		appointmentRepository.save(appointment);
		
		return appointment;
	}
	
	public Appointment deleteAppointment(int appointment_id) {
		Appointment appointment = appointmentRepository.findById(appointment_id);
		
		if(appointment == null) {
			return null;
		}
		
		appointmentRepository.delete(appointment);
		
		return appointment;
		
	}
}
