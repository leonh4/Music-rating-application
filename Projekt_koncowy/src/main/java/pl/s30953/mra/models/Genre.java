package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca gatunek muzyczny łącząca się asocjacjami z wydaniami i artystami
 */
@Entity
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name of genre cannot be blank")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "genres")
    private List<Artist> artists = new ArrayList<>();

    @ManyToMany(mappedBy = "genres")
    private List<Release> releases = new ArrayList<>();

    public Genre() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
