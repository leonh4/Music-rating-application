package pl.s30953.mra.models;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ReleaseTest {

    /**
     * Tworzy ocenę o podanej wartości punktowej.
     */
    private static Rating rating(int score) {
        Rating rating = new Rating();
        rating.setScore(score);
        return rating;
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
     * Nowe wydanie bez ocen ma średnią równą 0.
     */
    @Test
    void newReleaseHasZeroAverage() {
        Release release = new Release();

        assertThat(release.getAverageScore()).isEqualTo(0.0);
    }

    /**
     * Przeliczenie średniej przy pustej liście ocen daje 0.
     */
    @Test
    void calculateAverageScoreWithoutRatingsGivesZero() {
        Release release = new Release();

        release.calculateAverageScore();

        assertThat(release.getAverageScore()).isEqualTo(0.0);
    }

    /**
     * Przeliczenie średniej uwzględnia wszystkie oceny wydania.
     */
    @Test
    void calculateAverageScoreUsesAllRatings() {
        Release release = new Release();
        release.getRatings().add(rating(10));
        release.getRatings().add(rating(8));
        release.getRatings().add(rating(7));

        release.calculateAverageScore();

        assertThat(release.getAverageScore()).isCloseTo(8.333, within(0.001));
    }

    /**
     * Po usunięciu wszystkich ocen średnia wraca do 0.
     */
    @Test
    void calculateAverageScoreAfterClearingRatingsGivesZero() {
        Release release = new Release();
        release.getRatings().add(rating(9));
        release.calculateAverageScore();

        release.getRatings().clear();
        release.calculateAverageScore();

        assertThat(release.getAverageScore()).isEqualTo(0.0);
    }

    /**
     * Dwa niezapisane wydania (id = 0) nie są sobie równe, ale każde jest równe samemu sobie.
     */
    @Test
    void unsavedReleasesAreEqualOnlyToThemselves() {
        Release first = new Release();
        Release second = new Release();

        assertThat(first).isEqualTo(first);
        assertThat(first).isNotEqualTo(second);
    }

    /**
     * Wydania o tym samym identyfikatorze są równe i mają ten sam hashCode.
     */
    @Test
    void releasesWithSameIdAreEqual() {
        Release first = releaseWithId(5);
        Release second = releaseWithId(5);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Wydania o różnych identyfikatorach nie są równe.
     */
    @Test
    void releasesWithDifferentIdsAreNotEqual() {
        assertThat(releaseWithId(1)).isNotEqualTo(releaseWithId(2));
    }

    /**
     * Wydanie nie jest równe wartości null ani obiektowi innej klasy.
     */
    @Test
    void releaseIsNotEqualToNullOrOtherType() {
        Release release = releaseWithId(1);

        assertThat(release.equals(null)).isFalse();
        assertThat(release.equals("Abbey Road")).isFalse();
    }

    /**
     * Wydanie pozostaje odnajdywalne w zbiorze haszującym po nadaniu mu identyfikatora.
     */
    @Test
    void releaseStaysInHashSetAfterIdIsAssigned() {
        Release release = new Release();
        Set<Release> releases = new HashSet<>();
        releases.add(release);

        release.setId(10);

        assertThat(releases).contains(release);
    }
}
