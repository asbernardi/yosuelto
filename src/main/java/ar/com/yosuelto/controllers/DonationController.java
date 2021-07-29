package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Postulation;
import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.repositories.PostulationRepository;
import ar.com.yosuelto.repositories.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

@Controller
public class DonationController {

    private static final String FILES_PATH = "/home/adrian/cosas/yosuelto/images/publication/";

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PostulationRepository postulationRepository;

    @GetMapping("/")
    public String getPublications(Model model) {
        model.addAttribute("publications", publicationRepository.findAll());
        model.addAttribute("postulation", new Postulation());
        return "index";
    }

    @GetMapping("/donacion/imagen/{id}")
    public ResponseEntity<byte[]> getPublication(@PathVariable String id, Model model) {
        try {
            byte[] image = Files.readAllBytes(Paths.get(FILES_PATH + id).toAbsolutePath());
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            // TODO implementar log.
            e.printStackTrace();
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
        publicationRepository.save(publication);

        // TODO quitar hardcodeo de JPG:
        Path absolutePath = Paths.get(FILES_PATH + publication.getId() + ".jpg").toAbsolutePath();
        try {
            formFile.transferTo(absolutePath);
        } catch (IOException e) {
            // TODO implementar log.
            e.printStackTrace();
        }

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
}
