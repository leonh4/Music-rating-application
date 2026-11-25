package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakcyjna klasa bazowa reprezentująca twórcę muzycznego uczestniczy w realizacji dziedziczenia rozłącznego
 * strategią JOINED
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;

    @Size(max = 1000, message = "Biography cannot exceed 1000 characters")
    @Column(length = 1000)
    private String biography;

    /** Asocjacja wiele-do-wielu łącząca artystę z jego wydaniami */
    @ManyToMany
    @JoinTable(
            name = "artist_release",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "release_id")
    )
    private List<Release> releases = new ArrayList<>();

    /** Asocjacja wiele-do-wielu określająca gatunki muzyczne, w jakich porusza się artysta */
    @ManyToMany
    @JoinTable(
            name = "artist_genre",
            joinColumns = @JoinColumn(name = "artist_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    public Artist() {}

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
