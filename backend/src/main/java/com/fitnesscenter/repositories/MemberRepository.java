package com.fitnesscenter.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Appointment;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.models.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

	
	
	Optional<Member> findByEmail(String email);

}
