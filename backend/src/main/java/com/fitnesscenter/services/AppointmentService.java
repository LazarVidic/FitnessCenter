package com.fitnesscenter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fitnesscenter.dtos.AppointmentDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.LocationRepository;
import com.fitnesscenter.repositories.ServiceRepository;

@org.springframework.stereotype.Service
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
        Optional<Appointment> opt = appointmentRepository.findById(appointment_id);
        if (opt.isPresent()) return opt.get();
        return null;
    }

    public Appointment createAppointment(AppointmentDto appointmentDto) {

        Optional<Location> optLoc = locationRepository.findById(appointmentDto.getLocationId());
        Location location = null;
        if (optLoc.isPresent()) location = optLoc.get();

        Optional<com.fitnesscenter.models.Service> optSvc = serviceRepository.findById(appointmentDto.getServiceId());
        com.fitnesscenter.models.Service trainingService = null;
        if (optSvc.isPresent()) trainingService = optSvc.get();

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

        Optional<Appointment> optAppt = appointmentRepository.findById(appointment_id);
        Appointment appointment = null;
        if (optAppt.isPresent()) appointment = optAppt.get();
        if (appointment == null) return null;

        Optional<Location> optLoc = locationRepository.findById(appointmentDto.getLocationId());
        Location location = null;
        if (optLoc.isPresent()) location = optLoc.get();

        Optional<com.fitnesscenter.models.Service> optSvc = serviceRepository.findById(appointmentDto.getServiceId());
        com.fitnesscenter.models.Service trainingService = null;
        if (optSvc.isPresent()) trainingService = optSvc.get();

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
        Optional<Appointment> opt = appointmentRepository.findById(appointment_id);
        Appointment appointment = null;
        if (opt.isPresent()) appointment = opt.get();

        if (appointment == null) return null;

        appointmentRepository.delete(appointment);
        return appointment;
    }
}
