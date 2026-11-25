package pl.s30953.mra.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.s30953.mra.models.Release;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium dla encji Release. Zapewnia dostęp do bazy danych oraz zawiera dedykowane zapytania,
 * wykorzystywane przez główny moduł rankingów.
 */
@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {

    /**
     * Wyszukuje najwyżej ocenianie wydania w podanym przedziale czasowym.
     *
     * @param startDate Początek przedziału czasowego.
     * @param endDate Koniec przedziału czasowego.
     * @param minScore Minimalna średnia ocena wymagana do uwzględnienia w rankingu.
     * @param minRatings Minimalna liczba ocen wymagana do uwzględnienia w rankingu.
     * @return Posortowana malejąco lista wydań spełniających kryteria.
     */
    @Query("SELECT r FROM Release r WHERE r.dateOfRelease BETWEEN :startDate AND :endDate AND r.averageScore >= :minScore AND SIZE(r.ratings) >= :minRatings ORDER BY r.averageScore DESC")
    List<Release> findTopReleasesByYear(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("minScore") double minScore, @Param("minRatings") int minRatings);

    /**
     * Wyszukuje najwyżej oceniane wydania należące do wskazanego gatunku muzycznego.
     *
     * @param genreName Nazwa gatunku muzycznego.
     * @param minScore Minimalna średnia ocena wymagana do uwzględnienia w rankingu.
     * @param minRatings Minimalna liczba ocen wymagana do uwzględnienia w rankingu.
     * @return Posortowana malejąco lista wydań spełniających kryteria.
     */
    @Query("SELECT r FROM Release r JOIN r.genres g WHERE g.name = :genreName AND r.averageScore >= :minScore AND SIZE(r.ratings) >= :minRatings ORDER BY r.averageScore DESC")
    List<Release> findTopReleasesByGenre(@Param("genreName") String genreName, @Param("minScore") double minScore, @Param("minRatings") int minRatings);
}
