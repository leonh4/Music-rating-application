package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Encja reprezentująca pojedynczy utwór wchodzący w skład konkretnego wydania.
 */
@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Song duration is required")
    @Min(value = 1, message = "Song duration must be at least 1 second")
    private Integer duration;

    @NotNull(message = "Position on song list is required")
    @Min(value = 1, message = "Position on list must be greater than zero")
    private Integer positionOnList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    public Song() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getPositionOnList() {
        return positionOnList;
    }

    public Release getRelease() {
        return release;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle( String title) {
        this.title = title;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setPositionOnList( Integer positionOnList) {
        this.positionOnList = positionOnList;
    }

    public void setRelease(Release release) {
        this.release = release;
    }
}
