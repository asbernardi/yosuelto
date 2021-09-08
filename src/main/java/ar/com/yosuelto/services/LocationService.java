package ar.com.yosuelto.services;

import ar.com.yosuelto.clients.GeoIPClient;
import ar.com.yosuelto.model.Location;
import ar.com.yosuelto.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private GeoIPClient geoIPClient;

    public Location saveLocation(String ip) {
        Location location = geoIPClient.getLocation(ip);
        return locationRepository.save(location);
    }

    public Location getLocation(String ip) {
        return locationRepository.findByQuery(ip);
    }
}
