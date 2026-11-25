package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reprezentuje wydanie muzyczne (album, singiel, kompilacja). Łączy oceny, utwory, artystów i gatunki.
 */
@Entity
public class Release {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String title;

    private LocalDate dateOfRelease;

    private String language;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Enumerated(EnumType.STRING)
    private ReleaseType releaseType;

    @ElementCollection(targetClass = Tag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "release_tags", joinColumns = @JoinColumn(name  = "release_id"))
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Song> songs = new ArrayList<>();

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @ManyToMany(mappedBy = "releases")
    private List<Artist> artists = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "release_genre",
            joinColumns = @JoinColumn(name = "release_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres = new ArrayList<>();

    @ManyToMany(mappedBy = "rankedReleases")
    private List<Ranking> rankings = new ArrayList<>();

    public Release() {}

    /**
     * Wylicza i aktualizuje atrybut pochodny averageScore. Metoda jest uruchamiana za każdym razem, gdy do asocjacji
     * jest dodawana lub usuwana nowa ocena.
     */
    public void calculateAverageScore() {
        if (ratings == null || ratings.isEmpty()) {
            this.averageScore = 0.0;
        } else {
            double avg = ratings.stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);
            this.averageScore = avg;
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDateOfRelease() {
        return dateOfRelease;
    }

    public String getLanguage() {
        return language;
    }

    public ReleaseType getReleaseType() {
        return releaseType;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public List<Ranking> getRankings() {
        return rankings;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {this.title = title;}

    public void setDateOfRelease(LocalDate dateOfRelease) {
        this.dateOfRelease = dateOfRelease;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setReleaseType(ReleaseType releaseType) {
        this.releaseType = releaseType;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     * Wydania są porównywane po identyfikatorze nadanym przez bazę. Obiekty jeszcze niezapisane (id = 0) są równe
     * tylko samym sobie. Stały hashCode zapewnia poprawne działanie w kolekcjach także po nadaniu id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Release other)) return false;
        return getId() != 0 && getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Release.class.hashCode();
    }
}
