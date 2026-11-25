package pl.s30953.mra.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Specjalizacja klasy Ranking reprezentująca zestawienie najlepszych wydań dla konkretnego roku premiery.
 */
@Entity
public class YearRanking extends Ranking {
    @NotNull
    @Min(value = 1950)
    @Column(name = "ranking_year", nullable = false)
    private Integer year;

    public YearRanking() {}

    /**
     * Odświeża stan encji przez aktualizację daty wygenerowania zestawienia. Logika pobierania danych realizowana w RankingService.
     */
    @Override
    public void generateRanking() {
        this.setDateOfGeneration(LocalDate.now());
    }

    public Integer getYear() {
        return year;
    }

    public void setYear (Integer year) {
        this.year = year;
    }
}
