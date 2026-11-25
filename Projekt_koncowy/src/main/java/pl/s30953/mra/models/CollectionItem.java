package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Klasa asocjacyjna reprezentująca konkretny egzemplarz wydania w kolekcji użytkownika przechowuje informacje o dacie
 * dodania oraz formacie fizycznym/cyfrowym nośnika
 */
@Entity
public class CollectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Media type is required")
    @Column(nullable = false)
    private MediaType mediaType;

    @NotNull()
    private LocalDate addedDate;

    /** Powiązanie z wydaniem muzycznym (strona wiele w asocjacji 1-do-wielu) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    /** Właściciel danego elementu kolekcji */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CollectionItem() {}

    public int getId() {
        return id;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public Release getRelease() {
        return release;
    }

    public User getUser() { return user; }

    public void setId(int id) {
        this.id = id;
    }

    public void setMediaType(MediaType mediaType) {this.mediaType = mediaType;}

    public void setAddedDate(LocalDate addedDate) {this.addedDate = addedDate;}

    public void setRelease(Release release) {
        this.release = release;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
