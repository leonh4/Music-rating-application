package pl.s30953.mra.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.s30953.mra.dtos.RatingDto;
import pl.s30953.mra.dtos.ReleaseDto;
import pl.s30953.mra.models.Release;
import pl.s30953.mra.models.User;
import pl.s30953.mra.repositories.ReleaseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;

    private ReleaseService releaseService;

    /**
     * Tworzy serwis z atrapą repozytorium.
     */
    @BeforeEach
    void setUp() {
        releaseService = new ReleaseService(releaseRepository);
    }

    /**
     * Tworzy wydanie o podanym identyfikatorze i tytule.
     */
    private static Release release(int id, String title) {
        Release release = new Release();
        release.setId(id);
        release.setTitle(title);
        return release;
    }

    /**
     * Tworzy użytkownika o podanej nazwie.
     */
    private static User user(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }

    /**
     * Wszystkie wydania są mapowane na DTO z identyfikatorem, tytułem i średnią.
     */
    @Test
    void getAllReleasesMapsEntitiesToDtos() {
        Release abbeyRoad = release(1, "Abbey Road");
        user("Maciej1").rateRelease(10, abbeyRoad);
        user("Michal2").rateRelease(8, abbeyRoad);
        Release revolver = release(2, "Revolver");
        when(releaseRepository.findAll()).thenReturn(List.of(abbeyRoad, revolver));

        List<ReleaseDto> result = releaseService.getAllReleases();

        assertThat(result).containsExactly(
                new ReleaseDto(1, "Abbey Road", 9.0),
                new ReleaseDto(2, "Revolver", 0.0));
    }

    /**
     * Pusta baza daje pustą listę wydań.
     */
    @Test
    void getAllReleasesWithEmptyRepository() {
        when(releaseRepository.findAll()).thenReturn(List.of());

        assertThat(releaseService.getAllReleases()).isEmpty();
    }

    /**
     * Oceny wydania są mapowane na DTO z nazwą autora.
     */
    @Test
    void getRatingsForReleaseMapsRatingsToDtos() {
        Release abbeyRoad = release(1, "Abbey Road");
        user("Maciej1").rateRelease(10, abbeyRoad, "nicee");
        user("Michal2").rateRelease(8, abbeyRoad);
        when(releaseRepository.findById(1)).thenReturn(Optional.of(abbeyRoad));

        List<RatingDto> result = releaseService.getRatingsForRelease(1);

        assertThat(result).containsExactly(
                new RatingDto(10, "nicee", "Maciej1", LocalDate.now()),
                new RatingDto(8, null, "Michal2", LocalDate.now()));
    }

    /**
     * Wydanie bez ocen daje pustą listę ocen.
     */
    @Test
    void getRatingsForReleaseWithoutRatings() {
        when(releaseRepository.findById(2)).thenReturn(Optional.of(release(2, "Revolver")));

        assertThat(releaseService.getRatingsForRelease(2)).isEmpty();
    }

    /**
     * Brak wydania kończy się wyjątkiem IllegalArgumentException.
     */
    @Test
    void getRatingsForMissingReleaseThrows() {
        when(releaseRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> releaseService.getRatingsForRelease(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Release not found");
    }
}
