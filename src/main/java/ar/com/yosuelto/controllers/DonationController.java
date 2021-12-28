package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Location;
import ar.com.yosuelto.model.Postulation;
import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.repositories.PostulationRepository;
import ar.com.yosuelto.repositories.PublicationRepository;
import ar.com.yosuelto.services.ImageService;
import ar.com.yosuelto.services.LocationService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Controller
public class DonationController {

    public static final String ANY_COUNTRY = "Any country";
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PostulationRepository postulationRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LocationService locationService;

    @GetMapping("/")
    public String getPublications(Model model) {
        model.addAttribute("publications", publicationRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("location", new Location());
        model.addAttribute("countries", getCountries());
        model.addAttribute("postulation", new Postulation());
        return "index";
    }

    @PostMapping("/")
    public String getPublicationsByCountry(@ModelAttribute Location location, Model model) {
        List<Publication> publications;

        String country = location.getCountry();
        if (Strings.isEmpty(country) || ANY_COUNTRY.equals(country)) {
            publications = publicationRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        } else {
            publications = publicationRepository.findByLocationCountry(country, Sort.by(Sort.Direction.ASC, "id"));
        }

        model.addAttribute("publications", publications);
        model.addAttribute("location", location);
        model.addAttribute("countries", getCountries());
        model.addAttribute("postulation", new Postulation());
        return "index";
    }

    @GetMapping("/donacion/imagen/{id}")
    public ResponseEntity<byte[]> getPublication(@PathVariable String id, Model model) {
        // Este método sólo se usa localmente
        byte[] image = imageService.getLocalImage(id);

        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/donacion/donar")
    public String getPublishDonationPage(Model model) {
        model.addAttribute("publication", new Publication());
        return "donar";
    }

    @PostMapping("/donacion/soltar")
    public String postDonation(@ModelAttribute Publication publication, @RequestParam("formFile") MultipartFile formFile, Model model, HttpServletRequest request) {
        publication.setPublicationDate(Calendar.getInstance());

        String remoteAddr = request.getRemoteAddr();

        Location location = locationService.getLocation(remoteAddr);
        if (location == null) {
            location = locationService.saveLocation(remoteAddr);
        }
        publication.setLocation(location);

        // TODO por ahora es necesario guardar la publicacion para tener un ID. Luego con ese ID se puede subir la imagen.
        publicationRepository.save(publication);

        imageService.upload(publication, formFile);

        String url = imageService.getImageUrl(publication, formFile.getOriginalFilename().substring(formFile.getOriginalFilename().indexOf(".")));
        publication.setImageUrl(url);
        String optimizedImageUrl = imageService.getOptimizedImageUrl(publication, formFile.getOriginalFilename().substring(formFile.getOriginalFilename().indexOf(".")));
        publication.setOptimizedImageUrl(optimizedImageUrl);

        publicationRepository.save(publication);

        model.addAttribute("publications", publicationRepository.findAll());
        return "redirect:/";
    }

    @PostMapping("/donacion/postular")
    public String postPostulation(@ModelAttribute Postulation postulation, Model model) {
        if (postulation.getPublicationId() == null) {
            // TODO ¿Mostrar mensaje de error en pagina principal?
            return "redirect:/";
        }

        // TODO validar que no se haya postulado previamente el mismo usuario, a la misma publicacion


        Publication publication = publicationRepository.getOne(postulation.getPublicationId());
        postulation.setPublication(publication);

        postulation.setPostulationDate(Calendar.getInstance());
        postulationRepository.save(postulation);

        model.addAttribute("publications", publicationRepository.findAll());
        return "redirect:/";
    }

    @GetMapping("/donacion/{url}")
    public String getDonationPage(@PathVariable String url, Model model, @AuthenticationPrincipal OAuth2User principal) {
        System.out.println("Name: " + principal.getAttribute("name"));
        String id = url.substring(url.lastIndexOf("-")+1);
        Optional<Publication> publication = publicationRepository.findById(new Long(id));
        model.addAttribute("publication", publication.get());
        model.addAttribute("postulation", new Postulation());

        long donations = publicationRepository.countByEmail(publication.get().getEmail());
        model.addAttribute("donations", donations);
        return "donation";
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<byte[]> getFavicon() {
        byte[] image = new byte[0];

        try {
            image = Files.readAllBytes(Paths.get("favicon.ico"));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/favicon-56x56.png")
    public ResponseEntity<byte[]> getFavicon56() {
        byte[] image = new byte[0];

        try {
            image = Files.readAllBytes(Paths.get("favicon-56x56.png"));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/favicon-112x112.png")
    public ResponseEntity<byte[]> getFavicon112() {
        byte[] image = new byte[0];

        try {
            image = Files.readAllBytes(Paths.get("favicon-112x112.png"));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/manifest.webapp")
    public ResponseEntity<byte[]> getManifest() {
        byte[] manifest;

        try {
            manifest = Files.readAllBytes(Paths.get("manifest.webapp"));
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/x-web-app-manifest+json")).body(manifest);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/manifest.en-US.webapp")
    public ResponseEntity<byte[]> getManifestEnUs() {
        byte[] manifest;

        try {
            manifest = Files.readAllBytes(Paths.get("manifest.en-US.webapp"));
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/x-web-app-manifest+json")).body(manifest);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    private List<String> getCountries() {
        List<String> countries = Arrays.asList(ANY_COUNTRY, "Hong Kong", "South Africa", "India", "Argentina");
        return countries;
    }
}
