package pl.s30953.mra.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.s30953.mra.models.Ranking;

/**
 * Repozytorium zapewniające podstawowe operacje bazodanowe dla encji Ranking.
 */
@Repository
public interface RankingRepository extends JpaRepository<Ranking, Integer> {
}
