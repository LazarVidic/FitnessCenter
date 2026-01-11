package com.fitnesscenter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitnesscenter.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Optional<Reservation> findByStripeSessionId(String stripeSessionId);

    
    @Query("select count(r) from Reservation r where r.appointment.appointmentId = :appointmentId and r.status = 'PAID'")
    long countPaidByAppointmentId(@Param("appointmentId") int appointmentId);

    List<Reservation> findByMember_MemberIdOrderByCreatedAtDesc(int memberId);

    
    Reservation findByMember_MemberIdAndAppointment_AppointmentId(int memberId, int appointmentId);
}
