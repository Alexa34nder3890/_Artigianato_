package exam_project.artigiani_webapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "VIDEOS")
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(length = 1024)
    private String description;

    @Column(name = "video_url", nullable = false, length = 512)
    private String videoUrl;

    public VideoEntity() {}

    public VideoEntity(String title, String description, String videoUrl) {
        this.title       = title;
        this.description = description;
        this.videoUrl    = videoUrl;
    }

    // Getters and Setters
    public Long   getId()          { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getVideoUrl()    { return videoUrl; }

    public void setId(Long id)                   { this.id = id; }
    public void setTitle(String title)           { this.title = title; }
    public void setDescription(String desc)      { this.description = desc; }
    public void setVideoUrl(String videoUrl)     { this.videoUrl = videoUrl; }
}
