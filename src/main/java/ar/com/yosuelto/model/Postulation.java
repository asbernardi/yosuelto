package ar.com.yosuelto.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@Entity
public class Postulation {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Long publicationId;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    private String email;

    private Calendar postulationDate;

    public Postulation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(Long publicationId) {
        this.publicationId = publicationId;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getPostulationDate() {
        return postulationDate;
    }

    public void setPostulationDate(Calendar postulationDate) {
        this.postulationDate = postulationDate;
    }

    public String getPostulationDateSince() {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - postulationDate.getTimeInMillis();

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
