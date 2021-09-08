package ar.com.yosuelto.clients;

import ar.com.yosuelto.model.Location;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="geoip", url="http://ip-api.com/json")
public interface GeoIPClient {

    @RequestMapping(method= RequestMethod.GET, value="/{ip}?lang=es")
    Location getLocation(@PathVariable("ip") String ip);

}
