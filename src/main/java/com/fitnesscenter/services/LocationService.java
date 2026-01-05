package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitnesscenter.dtos.LocationDto;
import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.LocationRepository;
import com.fitnesscenter.repositories.MemberRepository;

@Service
public class LocationService {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	public List<Location> getAllLocations() {
		return locationRepository.findAll();
	}

	public Location getLocationById(int location_id) {
		Location location = locationRepository.findById(location_id);

		if (location == null) {
			return null;
		}

		return location;
	}

	public Location createLocation(LocationDto locationDto) {

		Location location = new Location();
		location.setLocationName(locationDto.getLocationName());
		location.setLocationAdress(locationDto.getLocationAdress());

		// Member (po uzoru na tvoj stil - bez null provere na DTO, ali sa proverom iz baze)
		// Ako ti LocationDto ima int member_id (ne Integer), onda koristi 0 kao "nema"
		if (locationDto.getMember_id() != 0) {
			Member member = memberRepository.findById(locationDto.getMember_id());
			if (member == null) {
				return null;
			}
			location.setMember(member);
		} else {
			location.setMember(null);
		}

		// Appointment
		if (locationDto.getAppointment_id() != 0) {
			Appointment appointment = appointmentRepository.findById(locationDto.getAppointment_id());
			if (appointment == null) {
				return null;
			}
			location.setAppointment(appointment);
		} else {
			location.setAppointment(null);
		}

		return locationRepository.save(location);
	}

	public Location updateLocation(int location_id, LocationDto locationDto) {
		Location location = locationRepository.findById(location_id);

		if (location == null) {
			return null;
		}

		location.setLocationName(locationDto.getLocationName());
		location.setLocationAdress(locationDto.getLocationAdress());

		// Member
		if (locationDto.getMember_id() != 0) {
			Member member = memberRepository.findById(locationDto.getMember_id());
			if (member == null) {
				return null;
			}
			location.setMember(member);
		} else {
			location.setMember(null);
		}

		// Appointment
		if (locationDto.getAppointment_id() != 0) {
			Appointment appointment = appointmentRepository.findById(locationDto.getAppointment_id());
			if (appointment == null) {
				return null;
			}
			location.setAppointment(appointment);
		} else {
			location.setAppointment(null);
		}

		locationRepository.save(location);

		return location;
	}

	public Location deleteLocation(int location_id) {
		Location location = locationRepository.findById(location_id);

		if (location == null) {
			return null;
		}

		locationRepository.delete(location);

		return location;
	}
}
