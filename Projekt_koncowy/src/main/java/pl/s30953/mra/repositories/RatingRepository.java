package pl.s30953.mra.repositories;

import pl.s30953.mra.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium zapewniające podstawowe operacje bazodanowe dla encji Rating.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
}
