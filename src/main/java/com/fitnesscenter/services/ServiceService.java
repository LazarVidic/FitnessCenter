package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fitnesscenter.dtos.ServiceDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Service;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	public Page<Service> findAll(Pageable pageable) {
		return serviceRepository.findAll(pageable);
	}

	// LIST search: WHERE nameService IN (...)
	public Page<Service> findByNameService(List<String> services, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return serviceRepository.findByNameServiceIn(services, pageable);
	}

	public Service getServiceById(int service_id) {
		Service service = serviceRepository.findById(service_id);
		if (service == null) {
			return null;
		}
		return service;
	}

	public Service createService(ServiceDto serviceDto) {

		Appointment appointment = appointmentRepository.findById(serviceDto.getAppointment_id());
		if (appointment == null) return null;

		Service service = new Service(serviceDto.getNameService(), serviceDto.getPrice());
		service.setAppointment(appointment);

		return serviceRepository.save(service);
	}

	public Service updateService(int service_id, ServiceDto serviceDto) {

		Service service = serviceRepository.findById(service_id);
		Appointment appointment = appointmentRepository.findById(serviceDto.getAppointment_id());

		if (service == null || appointment == null) {
			return null;
		}

		service.setNameService(serviceDto.getNameService());
		service.setPrice(serviceDto.getPrice());
		service.setAppointment(appointment);

		return serviceRepository.save(service);
	}

	public Service deleteService(int service_id) {
		Service service = serviceRepository.findById(service_id);

		if (service == null) {
			return null;
		}

		serviceRepository.delete(service);

		return service;
	}
}
