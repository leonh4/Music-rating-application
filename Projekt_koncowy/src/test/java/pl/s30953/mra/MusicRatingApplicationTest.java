package pl.s30953.mra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.s30953.mra.bootstrap.DataLoader;
import pl.s30953.mra.models.Artist;
import pl.s30953.mra.models.GenreRanking;
import pl.s30953.mra.models.Ranking;
import pl.s30953.mra.models.Release;
import pl.s30953.mra.models.YearRanking;
import pl.s30953.mra.repositories.ArtistRepository;
import pl.s30953.mra.repositories.RankingRepository;
import pl.s30953.mra.repositories.RatingRepository;
import pl.s30953.mra.repositories.ReleaseRepository;
import pl.s30953.mra.repositories.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:sqlite:target/test-integration.db")
class MusicRatingApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataLoader dataLoader;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RankingRepository rankingRepository;

    /**
     * Zwraca identyfikator wydania o podanym tytule z danych startowych.
     */
    private int releaseId(String title) {
        return releaseRepository.findAll().stream()
                .filter(release -> release.getTitle().equals(title))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    /**
     * DataLoader zapisuje komplet danych startowych.
     */
    @Test
    void dataLoaderSeedsDatabase() {
        assertThat(releaseRepository.count()).isEqualTo(15);
        assertThat(userRepository.count()).isEqualTo(4);
        assertThat(ratingRepository.count()).isEqualTo(4);
        assertThat(rankingRepository.count()).isEqualTo(2);
    }

    /**
     * Ponowne uruchomienie DataLoadera na zapełnionej bazie niczego nie dodaje.
     */
    @Test
    void dataLoaderDoesNotSeedTwice() throws Exception {
        dataLoader.run();

        assertThat(releaseRepository.count()).isEqualTo(15);
        assertThat(ratingRepository.count()).isEqualTo(4);
    }

    /**
     * Wszystkie wydania są powiązane z artystą po obu stronach asocjacji.
     */
    @Test
    @Transactional
    void releasesAreLinkedToArtist() {
        List<Artist> artists = artistRepository.findAll();

        assertThat(artists).hasSize(1);
        assertThat(artists.get(0).getName()).isEqualTo("The Beatles");
        assertThat(artists.get(0).getReleases()).hasSize(15);
        assertThat(releaseRepository.findAll())
                .allSatisfy(release -> assertThat(release.getArtists()).containsExactly(artists.get(0)));
    }

    /**
     * Oceny z danych startowych mają recenzje i poprawnie wyliczone średnie.
     */
    @Test
    @Transactional
    void seededRatingsHaveReviewsAndAverages() {
        Release abbeyRoad = releaseRepository.findById(releaseId("Abbey Road")).orElseThrow();
        Release rubberSoul = releaseRepository.findById(releaseId("Rubber Soul")).orElseThrow();

        assertThat(abbeyRoad.getAverageScore()).isEqualTo(9.0);
        assertThat(rubberSoul.getAverageScore()).isEqualTo(8.0);
        assertThat(ratingRepository.findAll()).allSatisfy(rating -> assertThat(rating.getReview()).isNotBlank());
    }

    /**
     * Ranking roczny 1969 zawiera tylko Abbey Road, a ranking rockowy oba ocenione albumy posortowane po średniej.
     */
    @Test
    @Transactional
    void seededRankingsContainExpectedReleases() {
        List<Ranking> rankings = rankingRepository.findAll();

        YearRanking yearRanking = rankings.stream()
                .filter(YearRanking.class::isInstance).map(YearRanking.class::cast).findFirst().orElseThrow();
        GenreRanking genreRanking = rankings.stream()
                .filter(GenreRanking.class::isInstance).map(GenreRanking.class::cast).findFirst().orElseThrow();

        assertThat(yearRanking.getYear()).isEqualTo(1969);
        assertThat(yearRanking.getRankedReleases()).extracting(Release::getTitle).containsExactly("Abbey Road");
        assertThat(genreRanking.getGenre().getName()).isEqualTo("Rock");
        assertThat(genreRanking.getRankedReleases()).extracting(Release::getTitle)
                .containsExactly("Abbey Road", "Rubber Soul");
    }

    /**
     * Endpoint listy wydań zwraca wszystkie wydania z bazy.
     */
    @Test
    void getAllReleasesEndpointReturnsSeededReleases() throws Exception {
        mockMvc.perform(get("/api/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(15))
                .andExpect(jsonPath("$[*].title", hasItem("Abbey Road")))
                .andExpect(jsonPath("$[?(@.title == 'Abbey Road')].averageRating", hasItem(9.0)));
    }

    /**
     * Endpoint ocen zwraca oceny wydania z nazwami autorów.
     */
    @Test
    void getRatingsEndpointReturnsRatingsOfRelease() throws Exception {
        mockMvc.perform(get("/api/releases/" + releaseId("Abbey Road") + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("Maciej1", "Michal2")))
                .andExpect(jsonPath("$[*].score", containsInAnyOrder(10, 8)));
    }

    /**
     * Endpoint ocen dla wydania bez ocen zwraca pustą tablicę.
     */
    @Test
    void getRatingsEndpointForUnratedReleaseReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/releases/" + releaseId("Revolver") + "/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Endpoint ocen dla nieistniejącego wydania zwraca 404.
     */
    @Test
    void getRatingsEndpointForMissingReleaseReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/releases/99999/ratings"))
                .andExpect(status().isNotFound());
    }

    /**
     * Strona główna GUI jest serwowana jako zasób statyczny.
     */
    @Test
    void indexPageIsServed() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk());
    }
}
