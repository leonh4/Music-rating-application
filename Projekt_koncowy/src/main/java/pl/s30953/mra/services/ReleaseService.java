package pl.s30953.mra.services;

import pl.s30953.mra.dtos.RatingDto;
import pl.s30953.mra.dtos.ReleaseDto;
import jakarta.transaction.Transactional;
import pl.s30953.mra.models.Release;
import org.springframework.stereotype.Service;
import pl.s30953.mra.repositories.ReleaseRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serwis dostarczający dane o wydaniach muzycznych dla warstwy kontrolerów. Odpowiada za mapowanie encji bazodanowych na
 * obiekty DTO.
 */
@Service
public class ReleaseService {
    private final ReleaseRepository releaseRepository;

    public ReleaseService(ReleaseRepository releaseRepository) {
        this.releaseRepository = releaseRepository;
    }

    /**
     * Zwraca uproszczoną listę wszystkich wydań muzycznych w systemie. Wykorzystywane do ładowania panelu głównego w GUI.
     *
     * @return Lista obiektów DTO zawierająca Id, tytuły i średnie oceny wydań.
     */
    public List<ReleaseDto> getAllReleases() {
        return releaseRepository.findAll().stream()
                .map(release -> new ReleaseDto(
                        release.getId(),
                        release.getTitle(),
                        release.getAverageScore()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę ocen i recenzji powiązanych z określonym. Ładuje leniwie elementy kolekcji w aktywnej sesji.
     *
     * @param releaseId Klucz główny wydania muzycznego.
     * @return Lista ocen w formie DTO przygotowanych do wyrenderowania GUI.
     */
    @Transactional()
    public List<RatingDto> getRatingsForRelease(Integer releaseId) {
        Release release = releaseRepository.findById(releaseId)
                .orElseThrow(() -> new IllegalArgumentException("Release not found"));

        return release.getRatings().stream()
                .map(rating -> new RatingDto(
                        rating.getScore(),
                        rating.getReview(),
                        rating.getUser().getUsername(),
                        rating.getDate()
                ))
                .collect(Collectors.toList());
    }
}
