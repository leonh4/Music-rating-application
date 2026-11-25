package pl.s30953.mra.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pl.s30953.mra.models.Genre;
import pl.s30953.mra.models.Release;
import pl.s30953.mra.models.ReleaseType;
import pl.s30953.mra.models.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:sqlite:target/test-repository.db")
class ReleaseRepositoryTest {

    private static final LocalDate START_1969 = LocalDate.of(1969, 1, 1);
    private static final LocalDate END_1969 = LocalDate.of(1969, 12, 31);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReleaseRepository releaseRepository;

    private User firstUser;
    private User secondUser;
    private Genre rock;
    private Genre jazz;

    /**
     * Przygotowuje dwóch użytkowników, dwa gatunki i zestaw wydań z różnymi datami, średnimi i liczbą ocen.
     * <ul>
     *     <li>Rock Start 1969 (1969-01-01, rock): oceny 10 i 8, średnia 9.0</li>
     *     <li>Rock End 1969 (1969-12-31, rock): ocena 6, średnia 6.0</li>
     *     <li>Rock Weak 1969 (1969-06-01, rock): ocena 3, średnia 3.0</li>
     *     <li>Rock Unrated 1969 (1969-06-01, rock): brak ocen</li>
     *     <li>Jazz 1969 (1969-05-01, jazz): oceny 7 i 7, średnia 7.0</li>
     *     <li>Rock 1970 (1970-01-01, rock): oceny 10 i 9, średnia 9.5</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        firstUser = persistUser("tester1");
        secondUser = persistUser("tester2");
        rock = persistGenre("Rock");
        jazz = persistGenre("Jazz");

        rate(persistRelease("Rock Start 1969", LocalDate.of(1969, 1, 1), rock), 10, 8);
        rate(persistRelease("Rock End 1969", LocalDate.of(1969, 12, 31), rock), 6);
        rate(persistRelease("Rock Weak 1969", LocalDate.of(1969, 6, 1), rock), 3);
        persistRelease("Rock Unrated 1969", LocalDate.of(1969, 6, 1), rock);
        rate(persistRelease("Jazz 1969", LocalDate.of(1969, 5, 1), jazz), 7, 7);
        rate(persistRelease("Rock 1970", LocalDate.of(1970, 1, 1), rock), 10, 9);

        entityManager.flush();
        entityManager.clear();
    }

    /**
     * Zapisuje poprawnego użytkownika o podanej nazwie.
     */
    private User persistUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("password123");
        return entityManager.persist(user);
    }

    /**
     * Zapisuje gatunek o podanej nazwie.
     */
    private Genre persistGenre(String name) {
        Genre genre = new Genre();
        genre.setName(name);
        return entityManager.persist(genre);
    }

    /**
     * Zapisuje wydanie o podanym tytule, dacie i gatunku.
     */
    private Release persistRelease(String title, LocalDate date, Genre genre) {
        Release release = new Release();
        release.setTitle(title);
        release.setDateOfRelease(date);
        release.setReleaseType(ReleaseType.STUDIO_ALBUM);
        release.getGenres().add(genre);
        return entityManager.persist(release);
    }

    /**
     * Wystawia wydaniu kolejne oceny, na zmianę od obu użytkowników.
     */
    private void rate(Release release, int... scores) {
        for (int i = 0; i < scores.length; i++) {
            User author = i % 2 == 0 ? firstUser : secondUser;
            author.rateRelease(scores[i], release);
        }
    }

    /**
     * Zwraca tytuły wydań w kolejności z listy.
     */
    private static List<String> titles(List<Release> releases) {
        return releases.stream().map(Release::getTitle).toList();
    }

    /**
     * Ranking roczny obejmuje cały rok łącznie z pierwszym i ostatnim dniem, pomija słabe i nieocenione wydania
     * oraz sortuje malejąco po średniej.
     */
    @Test
    void findTopReleasesByYearFiltersAndSorts() {
        List<Release> result = releaseRepository.findTopReleasesByYear(START_1969, END_1969, 5.0, 1);

        assertThat(titles(result)).containsExactly("Rock Start 1969", "Jazz 1969", "Rock End 1969");
    }

    /**
     * Ranking roczny pomija wydania, które mają mniej ocen niż wymagane minimum.
     */
    @Test
    void findTopReleasesByYearRespectsMinimumOfRatings() {
        List<Release> result = releaseRepository.findTopReleasesByYear(START_1969, END_1969, 5.0, 2);

        assertThat(titles(result)).containsExactly("Rock Start 1969", "Jazz 1969");
    }

    /**
     * Ranking roczny pomija wydania o średniej niższej od progu.
     */
    @Test
    void findTopReleasesByYearRespectsMinimumScore() {
        List<Release> result = releaseRepository.findTopReleasesByYear(START_1969, END_1969, 7.0, 1);

        assertThat(titles(result)).containsExactly("Rock Start 1969", "Jazz 1969");
    }

    /**
     * Przy zerowych progach ranking roczny zawiera wszystkie wydania z roku, także te bez ocen.
     */
    @Test
    void findTopReleasesByYearWithZeroThresholdsIncludesUnrated() {
        List<Release> result = releaseRepository.findTopReleasesByYear(START_1969, END_1969, 0.0, 0);

        assertThat(titles(result)).hasSize(5).contains("Rock Unrated 1969").doesNotContain("Rock 1970");
    }

    /**
     * Rok bez wydań daje pusty ranking.
     */
    @Test
    void findTopReleasesByYearWithoutReleasesIsEmpty() {
        List<Release> result = releaseRepository.findTopReleasesByYear(
                LocalDate.of(1980, 1, 1), LocalDate.of(1980, 12, 31), 5.0, 1);

        assertThat(result).isEmpty();
    }

    /**
     * Ranking gatunkowy obejmuje tylko wydania danego gatunku, niezależnie od roku, posortowane malejąco po średniej.
     */
    @Test
    void findTopReleasesByGenreFiltersAndSorts() {
        List<Release> result = releaseRepository.findTopReleasesByGenre("Rock", 5.0, 1);

        assertThat(titles(result)).containsExactly("Rock 1970", "Rock Start 1969", "Rock End 1969");
    }

    /**
     * Ranking gatunkowy pomija wydania, które mają mniej ocen niż wymagane minimum.
     */
    @Test
    void findTopReleasesByGenreRespectsMinimumOfRatings() {
        List<Release> result = releaseRepository.findTopReleasesByGenre("Rock", 5.0, 2);

        assertThat(titles(result)).containsExactly("Rock 1970", "Rock Start 1969");
    }

    /**
     * Ranking gatunkowy pomija wydania o średniej niższej od progu.
     */
    @Test
    void findTopReleasesByGenreRespectsMinimumScore() {
        List<Release> result = releaseRepository.findTopReleasesByGenre("Rock", 9.5, 1);

        assertThat(titles(result)).containsExactly("Rock 1970");
    }

    /**
     * Nieistniejący gatunek daje pusty ranking.
     */
    @Test
    void findTopReleasesByUnknownGenreIsEmpty() {
        assertThat(releaseRepository.findTopReleasesByGenre("Metal", 0.0, 0)).isEmpty();
    }

    /**
     * Średnia wyliczona przez rateRelease jest zapisywana w bazie razem z ocenami.
     */
    @Test
    void averageScoreAndRatingsArePersisted() {
        Release release = releaseRepository.findTopReleasesByGenre("Jazz", 0.0, 0).get(0);

        assertThat(release.getAverageScore()).isEqualTo(7.0);
        assertThat(release.getRatings()).hasSize(2);
    }
}
