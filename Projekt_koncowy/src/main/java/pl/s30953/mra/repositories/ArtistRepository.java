package pl.s30953.mra.repositories;

import pl.s30953.mra.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium zapewniające podstawowe operacje bazodanowe dla hierarchii klas Artist.
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
}
