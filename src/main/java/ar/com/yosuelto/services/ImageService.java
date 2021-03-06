package ar.com.yosuelto.services;

import ar.com.yosuelto.model.Publication;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class ImageService {

    @Autowired
    private Environment env;

    private static final String FILES_PATH = "/home/adrian/cosas/yosuelto/images/publication/";

    private static Cloudinary cloudinary;

    @Autowired
    public void ImageService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "yosuelto", "api_key", env.getProperty("API_KEY"), "api_secret", env.getProperty("API_SECRET")));
    }

    public void upload(Publication publication, MultipartFile formFile) {
        if ("LOCAL".equalsIgnoreCase(env.getProperty("yosuelto.upload.location"))) {
            this.localUpload(publication, formFile);
        } else {
            this.cloudinaryUpload(publication, formFile);
        }
    }

    private void localUpload(Publication publication, MultipartFile formFile) {
        String extension = formFile.getOriginalFilename().substring(formFile.getOriginalFilename().indexOf("."));
        Path absolutePath = Paths.get(FILES_PATH + publication.getId() + extension).toAbsolutePath();
        try {
            formFile.transferTo(absolutePath);
        } catch (IOException e) {
            // TODO implementar log.
            e.printStackTrace();
        }
    }

    private void cloudinaryUpload(Publication publication, MultipartFile formFile) {
        try {
            cloudinary.uploader().upload(formFile.getBytes(), ObjectUtils.asMap("public_id", publication.getId().toString()));
        } catch (IOException e) {
            // TODO implementar log.
            e.printStackTrace();
        }

    }

    public byte[] getLocalImage(String id) {
        try {
            return Files.readAllBytes(Paths.get(FILES_PATH + id).toAbsolutePath());
        } catch (IOException e) {
            // TODO implementar log.
            e.printStackTrace();
        }
        return null;
    }

    public String getImageUrl(Publication publication, String extension) {
        try {
            if ("LOCAL".equalsIgnoreCase(env.getProperty("yosuelto.upload.location"))) {
                return "http://localhost:8080/donacion/imagen/" + publication.getId() + extension;
            } else {
                Map options = ObjectUtils.asMap("secure","true");
                String url = cloudinary.api().resource(publication.getId().toString(), options).get("secure_url").toString();
                return url;
            }
        } catch (Exception e) {
            // TODO loguear bien
            e.printStackTrace();
        }
        // TODO devolver imagen generica si hubo un error.
        return null;
    }

    public String getOptimizedImageUrl(Publication publication, String extension, boolean webpFormat) {
        String environment = env.getProperty("yosuelto.upload.location");

        if ("local".equalsIgnoreCase(environment)) {
            return "http://localhost:8080/donacion/imagen/" + publication.getId() + extension;
        } else if ("cloudinary".equalsIgnoreCase(environment) && webpFormat){
            String url = publication.getImageUrl();
            url = url.replace("upload/", "upload/c_lpad,h_225,q_80,w_215/");
            return url.replace(url.substring(url.lastIndexOf(".")), ".webp");
        } else if ("cloudinary".equalsIgnoreCase(environment) && !webpFormat) {
            String url = publication.getImageUrl();
            url = url.replace("upload/", "upload/c_lpad,h_225,q_80,w_215/");
            return url.replace(url.substring(url.lastIndexOf(".")), ".jpg");
        } else {
            // TODO devolver imagen generica si hubo un error.
        }

        return null;
    }
}
