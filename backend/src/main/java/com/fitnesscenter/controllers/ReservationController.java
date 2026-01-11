package com.fitnesscenter.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fitnesscenter.models.Reservation;
import com.fitnesscenter.repositories.ReservationRepository;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationRepository reservationRepository;

	public ReservationController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
	@GetMapping("/my/{memberId}")
	public List<MyReservationDto> my(@PathVariable int memberId) {
		return reservationRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId).stream()
				.map(MyReservationDto::from).collect(Collectors.toList());
	}

	public static class MyReservationDto {
		public int reservationId;
		public int appointmentId;
		public String serviceName;
		public String locationName;
		public String startTime;
		public String endTime;
		public String status;

		static MyReservationDto from(Reservation r) {
			MyReservationDto dto = new MyReservationDto();
			dto.reservationId = r.getReservationId();
			dto.appointmentId = r.getAppointment().getAppointmentId();
			dto.serviceName = r.getAppointment().getService().getNameService();
			dto.locationName = r.getAppointment().getLocation().getLocationName();
			dto.startTime = r.getAppointment().getStartTime().toString();
			dto.endTime = r.getAppointment().getEndTime().toString();
			dto.status = r.getStatus();
			return dto;
		}
	}
}
