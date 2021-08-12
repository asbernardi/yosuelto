package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Postulation;
import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.repositories.PostulationRepository;
import ar.com.yosuelto.repositories.PublicationRepository;
import ar.com.yosuelto.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/")
    public String getPublications(Model model) {
        model.addAttribute("publications", publicationRepository.findAll());
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
    public String postDonation(@ModelAttribute Publication publication, @RequestParam("formFile") MultipartFile formFile, Model model) {
        publication.setPublicationDate(Calendar.getInstance());
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
        return "donation";
    }
}
