package com.fitnesscenter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

	Reservation findById(int reservation_id);
}
