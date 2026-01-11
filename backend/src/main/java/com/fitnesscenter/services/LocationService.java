package com.fitnesscenter.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.fitnesscenter.dtos.LocationDto;
import com.fitnesscenter.models.Location;
import com.fitnesscenter.repositories.LocationRepository;

@org.springframework.stereotype.Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(int location_id) {
        Optional<Location> opt = locationRepository.findById(location_id);
        if (opt.isPresent()) return opt.get();
        return null;
    }

    public Location createLocation(LocationDto locationDto) {
        Location location = new Location();
        location.setLocationName(locationDto.getLocationName());
        location.setLocationAddress(locationDto.getLocationAddress());
        return locationRepository.save(location);
    }

    public Location updateLocation(int location_id, LocationDto locationDto) {
        Optional<Location> opt = locationRepository.findById(location_id);
        Location location = null;
        if (opt.isPresent()) location = opt.get();

        if (location == null) return null;

        location.setLocationName(locationDto.getLocationName());
        location.setLocationAddress(locationDto.getLocationAddress());

        return locationRepository.save(location);
    }

    public Location deleteLocation(int location_id) {
        Optional<Location> opt = locationRepository.findById(location_id);
        Location location = null;
        if (opt.isPresent()) location = opt.get();

        if (location == null) return null;

        locationRepository.delete(location);
        return location;
    }
}
