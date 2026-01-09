package com.fitnesscenter.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fitnesscenter.dtos.LocationDto;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.services.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/locations")
@SecurityRequirement(name = "bearerAuth")
public class LocationController {

	@Autowired
	private LocationService locationService;

	// USER/SELLER/ADMIN: list
	@PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
	@GetMapping
	@Operation(summary = "Get all locations", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<List<Location>> getAllLocations() {
		List<Location> locations = locationService.getAllLocations();
		return new ResponseEntity<>(locations, HttpStatus.OK);
	}

	// USER/SELLER/ADMIN: get by id
	@PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
	@GetMapping("/{location_id}")
	@Operation(summary = "Get location by ID", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Location> getLocationById(@PathVariable int location_id) {
		Location location = locationService.getLocationById(location_id);
		if (location == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(location, HttpStatus.OK);
	}

	// SELLER/ADMIN: create
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	@PostMapping("/create")
	@Operation(summary = "Create location", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Location> createLocation(@RequestBody LocationDto locationDto) {
		Location created = locationService.createLocation(locationDto);
		if (created == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	// SELLER/ADMIN: update
	@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
	@PutMapping("/update/{location_id}")
	@Operation(summary = "Update location", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Location> updateLocation(@PathVariable int location_id, @RequestBody LocationDto locationDto) {
		Location updated = locationService.updateLocation(location_id, locationDto);
		if (updated == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}

	// ADMIN: delete
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/delete/{location_id}")
	@Operation(summary = "Delete location", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Location> deleteLocation(@PathVariable int location_id) {
		Location deleted = locationService.deleteLocation(location_id);
		if (deleted == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(deleted, HttpStatus.OK);
	}
}
