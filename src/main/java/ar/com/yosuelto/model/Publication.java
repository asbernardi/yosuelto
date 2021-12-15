package ar.com.yosuelto.model;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Entity
@Where(clause = "deleted is false")
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

    @Column(columnDefinition = "boolean default false")
    private boolean deleted;

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

    public String getOptimizedImageUrlWebp() {
        String jpgImage = this.imageUrl.replace("upload/", "upload/c_lpad,h_225,q_80,w_348/");
        return jpgImage.replace(".jpg", ".webp");
    }

    public String getOptimizedImageUrl() {
        return this.imageUrl.replace("upload/", "upload/c_lpad,h_225,q_80,w_348/");
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPublicationDateSinceKey() {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - publicationDate.getTimeInMillis();

        if (diff < 60000) {
            return "general.publication.date.since.seconds";
        } else if (diff < 3600000) {
            return "general.publication.date.since.minutes";
        } else if (diff < 86400000) {
            return "general.publication.date.since.hours";
        } else {
            return "general.publication.date.since.days";
        }
    }

    public String getPublicationDateSince() {
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - publicationDate.getTimeInMillis();

        if (diff < 60000) {
            return "";
        } else if (diff < 3600000) {
            return String.valueOf(TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS));
        } else if (diff < 86400000) {
            return String.valueOf(TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS));
        } else {
            return String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        }
    }

    public String getUrl() {
        return description
                .replace(" ", "-")
                .replace("?", "")
                .replace("/", "")
                .toLowerCase()
                .concat("-")
                .concat(id.toString());
    }

}
