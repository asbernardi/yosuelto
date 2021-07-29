package ar.com.yosuelto.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Entity
public class Publication {

    @Id
    @GeneratedValue
    private Long id;

    private String description;

    private Calendar publicationDate;

    public Publication() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getImageUrl() {
        // TODO revisar dominio para produccion y quitar hardcodeo de JPG:
        return "http://localhost:8080/donacion/imagen/" + this.getId() + ".jpg";
    }

    public String getPublicationDateSince() {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - publicationDate.getTimeInMillis();

        if (diff < 60000) {
            return "hace segundos";
        } else if (diff < 3600000) {
            return "hace " + TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) + " minutos";
        } else if (diff < 86400000) {
            return "hace " + TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS) + " horas";
        } else {
            return "hace " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + " días";
        }
    }

}
