package com.fitnesscenter.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitnesscenter.dtos.LocationDto;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.repositories.LocationRepository;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(int location_id) {
        return locationRepository.findById(location_id);
    }

    public Location createLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setLocationName(locationDto.getLocationName());
        location.setLocationAddress(locationDto.getLocationAddress());
        return locationRepository.save(location);
    }

    public Location updateLocation(int location_id, LocationDto locationDto) {
        Location location = locationRepository.findById(location_id);
        if (location == null) return null;

        location.setLocationName(locationDto.getLocationName());
        location.setLocationAddress(locationDto.getLocationAddress());

        return locationRepository.save(location);
    }

    public Location deleteLocation(int location_id) {
        Location location = locationRepository.findById(location_id);
        if (location == null) return null;

        locationRepository.delete(location);
        return location;
    }
}
