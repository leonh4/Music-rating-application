package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Klasa asocjacyjna reprezentująca konkretną ocenę (oraz opcjonalną recenzję) wystawioną danemu wydaniu przez użytkownika
 */
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Score is required")
    @Min(value = 1, message = "minimum score is 1")
    @Max(value = 10, message = "Maximum score is 10")
    @Column(nullable = false)
    private Integer score;

    @Size(max = 1000, message = "Review cannot exceed 1000 characters")
    @Column(length = 1000)
    private String review;

    @NotNull()
    private LocalDate date;

    /** Asocjacja wskazująca, jakiego wydania dotyczy ocena */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    /** Asocjacja wskazująca autora recenzji */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Rating() {}

    public int getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public String getReview() {
        return review;
    }

    public LocalDate getDate() {
        return date;
    }

    public Release getRelease() {
        return release;
    }

    public User getUser() {return user;}

    public void setId(int id) {
        this.id = id;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Oceny są porównywane po identyfikatorze nadanym przez bazę. Obiekty jeszcze niezapisane (id = 0) są równe
     * tylko samym sobie. Stały hashCode zapewnia poprawne działanie w kolekcjach także po nadaniu id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rating other)) return false;
        return getId() != 0 && getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Rating.class.hashCode();
    }
}
