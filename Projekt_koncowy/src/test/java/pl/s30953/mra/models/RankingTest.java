package pl.s30953.mra.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RankingTest {

    /**
     * Nowy ranking ma domyślne progi: co najmniej 1 ocena i średnia co najmniej 5.0.
     */
    @Test
    void newRankingHasDefaultThresholds() {
        YearRanking ranking = new YearRanking();

        assertThat(ranking.getMinimumOfRatings()).isEqualTo(1);
        assertThat(ranking.getMinimumScore()).isEqualTo(5.0);
    }

    /**
     * Progi rankingu można zmienić.
     */
    @Test
    void thresholdsCanBeChanged() {
        GenreRanking ranking = new GenreRanking();

        ranking.setMinimumOfRatings(3);
        ranking.setMinimumScore(8.0);

        assertThat(ranking.getMinimumOfRatings()).isEqualTo(3);
        assertThat(ranking.getMinimumScore()).isEqualTo(8.0);
    }

    /**
     * Wygenerowanie rankingu rocznego ustawia dzisiejszą datę generowania.
     */
    @Test
    void generateYearRankingSetsDateOfGeneration() {
        YearRanking ranking = new YearRanking();

        ranking.generateRanking();

        assertThat(ranking.getDateOfGeneration()).isEqualTo(LocalDate.now());
    }

    /**
     * Wygenerowanie rankingu gatunkowego ustawia dzisiejszą datę generowania.
     */
    @Test
    void generateGenreRankingSetsDateOfGeneration() {
        GenreRanking ranking = new GenreRanking();

        ranking.generateRanking();

        assertThat(ranking.getDateOfGeneration()).isEqualTo(LocalDate.now());
    }

    /**
     * Niezapisane rankingi są równe tylko samym sobie, a zapisane są porównywane po identyfikatorze.
     */
    @Test
    void rankingsAreComparedById() {
        YearRanking first = new YearRanking();
        YearRanking second = new YearRanking();

        assertThat(first).isEqualTo(first);
        assertThat(first).isNotEqualTo(second);

        first.setId(4);
        second.setId(4);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }
}
