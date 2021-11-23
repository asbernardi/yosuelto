package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Location;
import ar.com.yosuelto.model.Postulation;
import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.repositories.PostulationRepository;
import ar.com.yosuelto.repositories.PublicationRepository;
import ar.com.yosuelto.services.ImageService;
import ar.com.yosuelto.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Optional;

@Controller
public class DonationController {

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
        String url = imageService.getImageUrl(publication);

        publication.setImageUrl(url);
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
    public String getDonationPage(@PathVariable String url, Model model) {
        String id = url.substring(url.lastIndexOf("-")+1, url.length());
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

    @GetMapping("/manifest.webmanifest")
    public ResponseEntity<byte[]> getManifest() {
        byte[] manifest;

        try {
            manifest = Files.readAllBytes(Paths.get("manifest.webmanifest"));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(manifest);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }
}
