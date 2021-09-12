package ar.com.yosuelto.services;

import ar.com.yosuelto.clients.GeoIPClient;
import ar.com.yosuelto.model.Location;
import ar.com.yosuelto.repositories.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private GeoIPClient geoIPClient;

    private Logger logger = LoggerFactory.getLogger(LocationService.class);

    public Location saveLocation(String ip) {
        System.out.println("ip: " + ip);
        Location location = geoIPClient.getLocation(ip);
        System.out.println("location: " + location);
        return locationRepository.save(location);
    }

    public Location getLocation(String ip) {
        System.out.println("ip: " + ip);
        Location location = locationRepository.findByQuery(ip);
        System.out.println("location: " + location);
        System.out.println("location city: " + location.getCity());
        System.out.println("location status: " + location.getStatus());
        System.out.println("location country: " + location.getCountry());
        System.out.println("location region: " + location.getRegion());
        System.out.println("location ID: " + location.getId());
        return location;
    }
}
