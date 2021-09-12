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
        logger.info("IP: " + ip);
        Location location = geoIPClient.getLocation(ip);
        logger.info("Location: " + location);
        return locationRepository.save(location);
    }

    public Location getLocation(String ip) {
        logger.info("IP: " + ip);
        return locationRepository.findByQuery(ip);
    }
}
