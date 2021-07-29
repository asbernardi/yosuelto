package ar.com.yosuelto.model;

import javax.persistence.*;
import java.util.Calendar;

@Entity
public class Postulation {

    @Id
    @GeneratedValue
    private Long id;

    @Transient
    private Long publicationId;

    @ManyToOne
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

}
