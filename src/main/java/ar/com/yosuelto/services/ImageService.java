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
        // TODO quitar hardcodeo de JPG:
        Path absolutePath = Paths.get(FILES_PATH + publication.getId() + ".jpg").toAbsolutePath();
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

    public String getImageUrl(Publication publication) {
        try {
            if ("LOCAL".equalsIgnoreCase(env.getProperty("yosuelto.upload.location"))) {
                // TODO reemplazar .jpg
                return "http://localhost:8080/donacion/imagen/" + publication.getId() + ".jpg";
            } else {
                Map options = ObjectUtils.asMap("secure","true");
                String url = cloudinary.api().resource(publication.getId().toString(), options).get("secure_url").toString();
                url = url.replace("upload/", "upload/c_lpad,h_225,q_80,w_348/");
                return url.replace(".jpg", ".webp");
            }
        } catch (Exception e) {
            // TODO loguear bien
            e.printStackTrace();
        }
        // TODO devolver imagen generica si hubo un error.
        return null;
    }
}
