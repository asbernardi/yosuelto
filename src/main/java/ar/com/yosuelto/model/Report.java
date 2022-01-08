package ar.com.yosuelto.model;

import javax.persistence.*;
import java.util.Calendar;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Transient
    private Long publicationId;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    private String detail;

    private Calendar reportDate;

    public Report() {
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Calendar getReportDate() {
        return reportDate;
    }

    public void setReportDate(Calendar reportDate) {
        this.reportDate = reportDate;
    }

}
