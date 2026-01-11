package com.fitnesscenter.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Member;
import com.fitnesscenter.models.Reservation;
import com.fitnesscenter.repositories.AppointmentRepository;
import com.fitnesscenter.repositories.MemberRepository;
import com.fitnesscenter.repositories.ReservationRepository;

@Service
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final AppointmentRepository appointmentRepository;
  private final MemberRepository memberRepository;

  public ReservationService(ReservationRepository reservationRepository,
                            AppointmentRepository appointmentRepository,
                            MemberRepository memberRepository) {
    this.reservationRepository = reservationRepository;
    this.appointmentRepository = appointmentRepository;
    this.memberRepository = memberRepository;
  }

  public long remainingForAppointment(int appointmentId, int maxCapacity) {
    long reservedPaid = reservationRepository.countPaidByAppointmentId(appointmentId);
    return (long) maxCapacity - reservedPaid;
  }

  @Transactional
  public void createPaidReservation(int appointmentId, int memberId, String stripeSessionId) {

    // 1) idempotent
    Optional<Reservation> existingBySession = reservationRepository.findByStripeSessionId(stripeSessionId);
    if (existingBySession != null && existingBySession.isPresent()) {
      return;
    }

    // 2) već postoji rezervacija istog termina za istog usera
    Reservation existingSame =
        reservationRepository.findByMember_MemberIdAndAppointment_AppointmentId(memberId, appointmentId);

    if (existingSame != null) {
      existingSame.setStatus("PAID");
      existingSame.setStripeSessionId(stripeSessionId);
      reservationRepository.save(existingSame);
      return;
    }

    // 3) fetch appointment (bez orElseThrow)
    Optional<Appointment> optAppt = appointmentRepository.findById(appointmentId);
    Appointment appt = null;
    if (optAppt != null && optAppt.isPresent()) {
      appt = optAppt.get();
    }
    if (appt == null) {
      throw new IllegalArgumentException("Termin ne postoji.");
    }

    // 4) fetch member (bez orElseThrow)
    Optional<Member> optMember = memberRepository.findById(memberId);
    Member member = null;
    if (optMember != null && optMember.isPresent()) {
      member = optMember.get();
    }
    if (member == null) {
      throw new IllegalArgumentException("Member ne postoji.");
    }

    // 5) capacity check
    long reservedPaid = reservationRepository.countPaidByAppointmentId(appointmentId);
    if (reservedPaid >= appt.getMaxCapacity()) {
      throw new IllegalStateException("Termin je popunjen.");
    }

    // 6) create reservation
    Reservation r = new Reservation();
    r.setMember(member);
    r.setAppointment(appt);
    r.setStripeSessionId(stripeSessionId);
    r.setStatus("PAID");
    r.setCreatedAt(LocalDateTime.now());

    reservationRepository.save(r);
  }
}
