package ar.com.yosuelto.controllers;

import ar.com.yosuelto.model.Location;
import ar.com.yosuelto.model.Postulation;
import ar.com.yosuelto.model.Publication;
import ar.com.yosuelto.model.Report;
import ar.com.yosuelto.repositories.PostulationRepository;
import ar.com.yosuelto.repositories.PublicationRepository;
import ar.com.yosuelto.repositories.ReportRepository;
import ar.com.yosuelto.services.ImageService;
import ar.com.yosuelto.services.LocationService;
import org.apache.logging.log4j.util.Strings;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Controller
public class DonationController {

    public static final String ANY_COUNTRY = "Any country";
    private static final int REPORT_LIMIT = 5;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PostulationRepository postulationRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LocationService locationService;

    @GetMapping("/")
    public String getPublications(Model model) {
        model.addAttribute("publications", publicationRepository.findByReportsLessThan(REPORT_LIMIT, Sort.by(Sort.Direction.ASC, "id")));
        model.addAttribute("location", new Location());
        model.addAttribute("countries", getCountries());
        model.addAttribute("postulation", new Postulation());
        model.addAttribute("report", new Report());
        return "index";
    }

    @PostMapping("/")
    public String getPublicationsByCountry(@ModelAttribute Location location, Model model) {
        List<Publication> publications;

        String country = location.getCountry();
        if (Strings.isEmpty(country) || ANY_COUNTRY.equals(country)) {
            publications = publicationRepository.findByReportsLessThan(5, Sort.by(Sort.Direction.ASC, "id"));
        } else {
            publications = publicationRepository.findByLocationCountryAndReportsLessThan(country, 5, Sort.by(Sort.Direction.ASC, "id"));
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

        String extension = formFile.getOriginalFilename().substring(formFile.getOriginalFilename().indexOf("."));

        String url = imageService.getImageUrl(publication, extension);
        publication.setImageUrl(url);

        String optimizedImageUrl = imageService.getOptimizedImageUrl(publication, extension, true);
        publication.setOptimizedImageUrl(optimizedImageUrl);

        String optimizedImageUrlJpg = imageService.getOptimizedImageUrl(publication, extension, false);
        publication.setOptimizedImageUrlJpg(optimizedImageUrlJpg);

        publicationRepository.save(publication);

        model.addAttribute("publications", publicationRepository.findByReportsLessThan(5));
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

        model.addAttribute("publications", publicationRepository.findByReportsLessThan(REPORT_LIMIT));
        return "redirect:/";
    }

    @PostMapping("/donacion/denunciar")
    public String postPostulation(@ModelAttribute Report report, Model model) {
        if (report.getPublicationId() == null) {
            // TODO ¿Mostrar mensaje de error en pagina principal?
            return "redirect:/";
        }

        // TODO validar que no haya denunciado previamente el mismo usuario, a la misma publicacion.

        Publication publication = publicationRepository.getOne(report.getPublicationId());
        publication.addReport();
        publicationRepository.save(publication);

        report.setPublication(publication);
        report.setReportDate(Calendar.getInstance());
        reportRepository.save(report);

        model.addAttribute("publications", publicationRepository.findByReportsLessThan(REPORT_LIMIT));
        return "redirect:/";
    }

    @GetMapping("/donacion/{url}")
    public String getDonationPage(@PathVariable String url, Model model) {
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
        return getManifest("manifest.webapp");
    }

    @GetMapping(value = "/manifest.webmanifest")
    public ResponseEntity<byte[]> getManifest3() {
        return getManifest("manifest.webmanifest");
    }

    @GetMapping(value = "/manifest.en-US.webapp")
    public ResponseEntity<byte[]> getManifestEnUs() {
        return getManifest("manifest.en-US.webapp");
    }

    @GetMapping(value = "/manifest.en-US.webmanifest")
    public ResponseEntity<byte[]> getManifest3EnUS() {
        return getManifest("manifest.en-US.webmanifest");
    }

    @GetMapping(value = "/manifest.es-AR.webapp")
    public ResponseEntity<byte[]> getManifestEsAr() {
        return getManifest("manifest.es-AR.webapp");
    }

    private ResponseEntity<byte[]> getManifest(String manifestFile) {
        try {
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/x-web-app-manifest+json")).body(Files.readAllBytes(Paths.get(manifestFile)));
        } catch (Exception e) {
            // TODO replace with log:
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/app-ads.txt")
    public ResponseEntity<byte[]> getAppAds() {
        byte[] appAdsFile;

        try {
            appAdsFile = Files.readAllBytes(Paths.get("app-ads.txt"));
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/x-web-app-manifest+json")).body(appAdsFile);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/sitemap.xml")
    public ResponseEntity<byte[]> getSitemap() {
        try {
            byte[] sitemapFile = Files.readAllBytes(Paths.get("sitemap.xml"));
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/xml")).body(sitemapFile);
        } catch (IOException e) {
            // TODO replace with log:
            e.printStackTrace();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/robots.txt")
    public ResponseEntity<byte[]> getRobots() {
        try {
            byte[] robotsFile = Files.readAllBytes(Paths.get("robots.txt"));
            return ResponseEntity.ok().contentType(MediaType.valueOf("text/plain")).body(robotsFile);
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
