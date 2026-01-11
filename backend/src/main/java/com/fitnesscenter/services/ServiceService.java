package com.fitnesscenter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fitnesscenter.dtos.ServiceDto;
import com.fitnesscenter.models.Service;
import com.fitnesscenter.repositories.ServiceRepository;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Page<Service> findAll(Pageable pageable) {
        return serviceRepository.findAll(pageable);
    }

    public Page<Service> findByNameService(List<String> services, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return serviceRepository.findByNameServiceIn(services, pageable);
    }

    public Service getServiceById(int service_id) {
        Optional<Service> opt = serviceRepository.findById(service_id);
        if (opt.isPresent()) {
            return opt.get();
        }
        return null;
    }

    public Service createService(ServiceDto serviceDto) {
        Service service = new Service(serviceDto.getNameService(), serviceDto.getPrice());
        return serviceRepository.save(service);
    }

    public Service updateService(int service_id, ServiceDto serviceDto) {
        Optional<Service> opt = serviceRepository.findById(service_id);
        Service service = null;

        if (opt.isPresent()) {
            service = opt.get();
        }

        if (service == null) return null;

        service.setNameService(serviceDto.getNameService());
        service.setPrice(serviceDto.getPrice());

        return serviceRepository.save(service);
    }

    public Service deleteService(int service_id) {
        Optional<Service> opt = serviceRepository.findById(service_id);
        Service service = null;

        if (opt.isPresent()) {
            service = opt.get();
        }

        if (service == null) return null;

        serviceRepository.delete(service);
        return service;
    }
}
