package pl.s30953.mra.controllers;

import pl.s30953.mra.dtos.RatingDto;
import pl.s30953.mra.dtos.ReleaseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.s30953.mra.services.ReleaseService;

import java.util.List;

/**
 * Kontroler REST odpowiadający za obsługę żądań dotyczących wydań muzycznych
 */
@RestController
@RequestMapping("api/releases")
@CrossOrigin(origins = "*")
public class ReleaseController {
    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    /**
     * Pobiera pełną listę wydań muzycznych dostępnych w systemie
     *
     * @return Lista obiektów typu ReleaseDto zawierających podstawowe informacje o wydaniu
     */
    @GetMapping
    public ResponseEntity<List<ReleaseDto>> getAllReleases() {
        List<ReleaseDto> releases = releaseService.getAllReleases();
        return ResponseEntity.ok(releases);
    }

    /**
     * Realizuje nawigację po asocjacji: pobiera listę ocen powiązanych z konkretnym wydaniem
     *
     * @param id Identyfikator wydania muzycznego
     * @return Lista ocen (RatingDto) lub 404 Not Found w przypadku braku wydania
     */
    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<RatingDto>> getRatingsForRelease(@PathVariable int id) {
        try {
            List<RatingDto> ratings = releaseService.getRatingsForRelease(id);
            return ResponseEntity.ok(ratings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
