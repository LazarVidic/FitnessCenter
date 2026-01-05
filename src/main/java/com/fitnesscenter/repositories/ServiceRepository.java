package com.fitnesscenter.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnesscenter.models.Service;

@Repository
public interface ServiceRepository extends  JpaRepository<Service, Integer>{

	Service findById(int service_id);
	
	Page<Service> findAll(Pageable pageable);
	
	Page<Service> findByNameServiceIn(List<String> services, Pageable pageable );
	
}
