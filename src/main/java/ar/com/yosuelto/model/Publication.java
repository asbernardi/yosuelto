package ar.com.yosuelto.model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
public class Publication {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String description;

    // Donor's email address.
    private String email;

    @Column(name = "publication_date")
    private Calendar publicationDate;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "publication")
    private List<Postulation> postulations;

    @ManyToOne
    private Location location;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Postulation> getPostulations() {
        return postulations;
    }

    public void setPostulations(List<Postulation> postulations) {
        this.postulations = postulations;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public String getUrl() {
        return description
                .replace(" ", "-")
                .replace("?", "")
                .toLowerCase()
                .concat("-")
                .concat(id.toString());
    }

}
