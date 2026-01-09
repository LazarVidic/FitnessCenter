package com.fitnesscenter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.fitnesscenter.dtos.ServiceDto;
import com.fitnesscenter.services.ServiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/services")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {

	@Autowired
	private ServiceService serviceService;

	// ADMIN/SELLER: paged list
	@PreAuthorize("hasAnyRole('ADMIN','SELLER', 'USER')")
	@GetMapping
	@Operation(summary = "Get all services (paged)", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Page<com.fitnesscenter.models.Service>> getAllServices(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);
		Page<com.fitnesscenter.models.Service> services = serviceService.findAll(pageable);
		return new ResponseEntity<>(services, HttpStatus.OK);
	}

	// ADMIN/SELLER: search by nameService (paged)
	@PreAuthorize("hasAnyRole('ADMIN','SELLER', 'USER')")
	@GetMapping("/search")
	@Operation(summary = "Search services by nameService (paged)", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Page<com.fitnesscenter.models.Service>> searchByNameService(
			@RequestParam List<String> services,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		Page<com.fitnesscenter.models.Service> result = serviceService.findByNameService(services, page, size);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// ADMIN/SELLER: get by id
	@PreAuthorize("hasAnyRole('ADMIN','SELLER', 'USER')")
	@GetMapping("/{service_id}")
	@Operation(summary = "Get service by ID", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<com.fitnesscenter.models.Service> getServiceById(@PathVariable int service_id) {
		com.fitnesscenter.models.Service service = serviceService.getServiceById(service_id);
		if (service == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(service, HttpStatus.OK);
	}

	// ADMIN/SELLER: create
	@PreAuthorize("hasAnyRole('ADMIN','SELLER')")
	@PostMapping("/create")
	@Operation(summary = "Create service", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<com.fitnesscenter.models.Service> createService(@RequestBody ServiceDto serviceDto) {
		com.fitnesscenter.models.Service created = serviceService.createService(serviceDto);
		if (created == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	// ADMIN/SELLER: update
	@PreAuthorize("hasAnyRole('ADMIN','SELLER')")
	@PutMapping("/update/{service_id}")
	@Operation(summary = "Update service", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<com.fitnesscenter.models.Service> updateService(
			@PathVariable int service_id,
			@RequestBody ServiceDto serviceDto
	) {
		com.fitnesscenter.models.Service updated = serviceService.updateService(service_id, serviceDto);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	// ADMIN: delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{service_id}")
	@Operation(summary = "Delete service", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<com.fitnesscenter.models.Service> deleteService(@PathVariable int service_id) {
		com.fitnesscenter.models.Service deleted = serviceService.deleteService(service_id);
		if (deleted == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
}
