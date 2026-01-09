package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitnesscenter.dtos.AppointmentDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.LocationRepository;
import com.fitnesscenter.repositories.ServiceRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(int appointment_id) {
        return appointmentRepository.findById(appointment_id);
    }

    public Appointment createAppointment(AppointmentDto appointmentDto) {

        Location location = locationRepository.findById(appointmentDto.getLocationId());
        com.fitnesscenter.models.Service trainingService =
                serviceRepository.findById(appointmentDto.getServiceId());

        if (location == null || trainingService == null) {
            return null;
        }

        Appointment appointment = new Appointment();
        appointment.setStartTime(appointmentDto.getStartTime());
        appointment.setEndTime(appointmentDto.getEndTime());
        appointment.setMaxCapacity(appointmentDto.getMaxCapacity());
        appointment.setLocation(location);
        appointment.setService(trainingService);

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(int appointment_id, AppointmentDto appointmentDto) {

        Appointment appointment = appointmentRepository.findById(appointment_id);
        if (appointment == null) return null;

        Location location = locationRepository.findById(appointmentDto.getLocationId());
        com.fitnesscenter.models.Service trainingService =
                serviceRepository.findById(appointmentDto.getServiceId());

        if (location == null || trainingService == null) {
            return null;
        }

        appointment.setStartTime(appointmentDto.getStartTime());
        appointment.setEndTime(appointmentDto.getEndTime());
        appointment.setMaxCapacity(appointmentDto.getMaxCapacity());
        appointment.setLocation(location);
        appointment.setService(trainingService);

        return appointmentRepository.save(appointment);
    }

    public Appointment deleteAppointment(int appointment_id) {
        Appointment appointment = appointmentRepository.findById(appointment_id);
        if (appointment == null) return null;

        appointmentRepository.delete(appointment);
        return appointment;
    }
}
