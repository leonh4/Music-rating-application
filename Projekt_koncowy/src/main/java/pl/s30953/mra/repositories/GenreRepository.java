package pl.s30953.mra.repositories;

import pl.s30953.mra.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium zapewniające podstawowe operacje bazodanowe dla encji Genre.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
