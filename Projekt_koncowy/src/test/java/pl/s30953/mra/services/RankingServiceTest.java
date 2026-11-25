package pl.s30953.mra.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s30953.mra.models.Genre;
import pl.s30953.mra.models.GenreRanking;
import pl.s30953.mra.models.Release;
import pl.s30953.mra.models.YearRanking;
import pl.s30953.mra.repositories.RankingRepository;
import pl.s30953.mra.repositories.ReleaseRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private RankingRepository rankingRepository;

    private RankingService rankingService;

    /**
     * Tworzy serwis z atrapami repozytoriów.
     */
    @BeforeEach
    void setUp() {
        rankingService = new RankingService(releaseRepository, rankingRepository);
    }

    /**
     * Tworzy wydanie o podanym identyfikatorze.
     */
    private static Release releaseWithId(int id) {
        Release release = new Release();
        release.setId(id);
        return release;
    }

    /**
     * Tworzy ranking gatunkowy dla gatunku o podanej nazwie.
     */
    private static GenreRanking genreRanking(String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);
        GenreRanking ranking = new GenreRanking();
        ranking.setGenre(genre);
        return ranking;
    }

    /**
     * Ranking roczny pyta o wydania z całego roku z domyślnymi progami i dopina je do rankingu po obu stronach.
     */
    @Test
    void generateYearRankingLinksReleasesFromWholeYear() {
        YearRanking ranking = new YearRanking();
        ranking.setYear(1969);
        Release first = releaseWithId(1);
        Release second = releaseWithId(2);
        when(releaseRepository.findTopReleasesByYear(
                LocalDate.of(1969, 1, 1), LocalDate.of(1969, 12, 31), 5.0, 1))
                .thenReturn(List.of(first, second));

        rankingService.generateYearRanking(ranking);

        assertThat(ranking.getRankedReleases()).containsExactly(first, second);
        assertThat(first.getRankings()).containsExactly(ranking);
        assertThat(second.getRankings()).containsExactly(ranking);
        assertThat(ranking.getDateOfGeneration()).isEqualTo(LocalDate.now());
        verify(rankingRepository).save(ranking);
    }

    /**
     * Ranking roczny przekazuje do zapytania własne progi rankingu.
     */
    @Test
    void generateYearRankingUsesRankingThresholds() {
        YearRanking ranking = new YearRanking();
        ranking.setYear(2000);
        ranking.setMinimumScore(8.0);
        ranking.setMinimumOfRatings(3);

        rankingService.generateYearRanking(ranking);

        verify(releaseRepository).findTopReleasesByYear(
                LocalDate.of(2000, 1, 1), LocalDate.of(2000, 12, 31), 8.0, 3);
        assertThat(ranking.getRankedReleases()).isEmpty();
        verify(rankingRepository).save(ranking);
    }

    /**
     * Wydanie, które już jest powiązane z rankingiem, nie dostaje go drugi raz.
     */
    @Test
    void generateYearRankingDoesNotDuplicateLink() {
        YearRanking ranking = new YearRanking();
        ranking.setYear(1969);
        Release release = releaseWithId(1);
        release.getRankings().add(ranking);
        when(releaseRepository.findTopReleasesByYear(
                LocalDate.of(1969, 1, 1), LocalDate.of(1969, 12, 31), 5.0, 1))
                .thenReturn(List.of(release));

        rankingService.generateYearRanking(ranking);

        assertThat(release.getRankings()).containsExactly(ranking);
    }

    /**
     * Ranking gatunkowy pyta o wydania gatunku z domyślnymi progami i dopina je do rankingu po obu stronach.
     */
    @Test
    void generateGenreRankingLinksReleasesOfGenre() {
        GenreRanking ranking = genreRanking("Rock");
        Release first = releaseWithId(1);
        Release second = releaseWithId(2);
        when(releaseRepository.findTopReleasesByGenre("Rock", 5.0, 1)).thenReturn(List.of(first, second));

        rankingService.generateGenreRanking(ranking);

        assertThat(ranking.getRankedReleases()).containsExactly(first, second);
        assertThat(first.getRankings()).containsExactly(ranking);
        assertThat(second.getRankings()).containsExactly(ranking);
        assertThat(ranking.getDateOfGeneration()).isEqualTo(LocalDate.now());
        verify(rankingRepository).save(ranking);
    }

    /**
     * Ranking gatunkowy przekazuje do zapytania własne progi rankingu.
     */
    @Test
    void generateGenreRankingUsesRankingThresholds() {
        GenreRanking ranking = genreRanking("Jazz");
        ranking.setMinimumScore(7.5);
        ranking.setMinimumOfRatings(2);

        rankingService.generateGenreRanking(ranking);

        verify(releaseRepository).findTopReleasesByGenre("Jazz", 7.5, 2);
        verify(rankingRepository).save(ranking);
    }
}
