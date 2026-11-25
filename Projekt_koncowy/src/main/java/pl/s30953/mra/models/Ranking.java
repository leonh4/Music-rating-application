package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakcyjna klasa bazowa dla rankingów wydań muzycznych. Reprezentuje asocjację do wyróżnionych wydań, spełniających
 * określone wymagania punktowe.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull()
    private LocalDate dateOfGeneration;

    /** Minimalna liczba ocen, jaką musi mieć wydanie, aby trafić do rankingu */
    @Column(nullable = false)
    private int minimumOfRatings = 1;

    /** Minimalna średnia ocena, jaką musi mieć wydanie, aby trafić do rankingu */
    @Column(nullable = false)
    private double minimumScore = 5.0;

    @ManyToMany
    @JoinTable(
            name = "ranking_release",
            joinColumns = @JoinColumn(name = "ranking_id"),
            inverseJoinColumns = @JoinColumn(name = "release_id")
    )
    private List<Release> rankedReleases = new ArrayList<>();

    public Ranking() {}

    public abstract void generateRanking();

    public void setMinimumOfRatings(int minimumOfRatings) {
        this.minimumOfRatings = minimumOfRatings;
    }

    public double getMinimumScore() {
        return minimumScore;
    }

    public void setMinimumScore(double minimumScore) {
        this.minimumScore = minimumScore;
    }

    /**
     * Rankingi są porównywane po identyfikatorze nadanym przez bazę. Obiekty jeszcze niezapisane (id = 0) są równe
     * tylko samym sobie. Stały hashCode zapewnia poprawne działanie w kolekcjach także po nadaniu id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ranking other)) return false;
        return getId() != 0 && getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Ranking.class.hashCode();
    }

    public int getId() {
        return id;
    }

    public LocalDate getDateOfGeneration() {
        return dateOfGeneration;
    }

    public int getMinimumOfRatings() {
        return minimumOfRatings;
    }

    public List<Release> getRankedReleases() {
        return rankedReleases;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateOfGeneration(LocalDate dateOfGeneration) {this.dateOfGeneration = dateOfGeneration;}

    public void setRankedReleases(List<Release> rankedReleases) {
        this.rankedReleases = rankedReleases;
    }
}
