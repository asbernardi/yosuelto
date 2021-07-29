package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.repositories.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DonationRestController {

    @Autowired
    private PublicationRepository publicationRepository;

    @PostMapping("/publication")
    public Publication postPublication(@RequestBody Publication publication) {
        return publicationRepository.save(publication);
    }

    @GetMapping("/publications")
    public List<Publication> getPublications(Model model) {
        return publicationRepository.findAll();
    }

}
