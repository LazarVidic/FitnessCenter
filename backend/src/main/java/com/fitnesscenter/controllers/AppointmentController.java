package com.fitnesscenter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fitnesscenter.dtos.AppointmentDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.services.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/appointments")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	// USER/SELLER/ADMIN: list
	@PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
	@GetMapping
	@Operation(summary = "Get all appointments", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<List<Appointment>> getAllAppointments() {
		List<Appointment> appointments = appointmentService.getAllAppointments();
		return new ResponseEntity<>(appointments, HttpStatus.OK);
	}

	// USER/SELLER/ADMIN: get by id
	@PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
	@GetMapping("/{appointment_id}")
	@Operation(summary = "Get appointment by ID", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Appointment> getAppointmentById(@PathVariable int appointment_id) {
		Appointment appointment = appointmentService.getAppointmentById(appointment_id);
		if (appointment == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(appointment, HttpStatus.OK);
	}

	// SELLER/ADMIN: create
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	@PostMapping("/create")
	@Operation(summary = "Create appointment", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDto appointmentDto) {
		Appointment created = appointmentService.createAppointment(appointmentDto);
		if (created == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	// SELLER/ADMIN: update
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	@PutMapping("/update/{appointment_id}")
	@Operation(summary = "Update appointment", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Appointment> updateAppointment(
			@PathVariable int appointment_id,
			@RequestBody AppointmentDto appointmentDto
	) {
		Appointment updated = appointmentService.updateAppointment(appointment_id, appointmentDto);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	// ADMIN: delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{appointment_id}")
	@Operation(summary = "Delete appointment", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Appointment> deleteAppointment(@PathVariable int appointment_id) {
		Appointment deleted = appointmentService.deleteAppointment(appointment_id);
		if (deleted == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
}
