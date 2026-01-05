package com.fitnesscenter.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

	Location findById(int locationId);

    Page<Location> findByLocationNameContainingIgnoreCase(String locationName, Pageable pageable);

    Page<Location> findByAppointment_AppointmentIdIn(List<Integer> appointmentIds, Pageable pageable);

    Page<Location> findByMember_MemberIdIn(List<Integer> memberIds, Pageable pageable);

}
