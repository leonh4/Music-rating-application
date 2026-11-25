package pl.s30953.mra.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Specjalizacja klasy Ranking, reprezentująca zestawienie najlepszych wydań dla konkretnego gatunku
 */
@Entity
public class GenreRanking extends Ranking {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    public GenreRanking() {}

    /**
     * Implementacja metody abstrakcyjnej z klasy bazowej, która aktualizuje date wygenerowania rankingu na moment
     * jej wywołania. Logika pobierania danych realizowana w RankingService.
     */
    @Override
    public void generateRanking() {
        this.setDateOfGeneration(LocalDate.now());
    }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
}
