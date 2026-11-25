package pl.s30953.mra.repositories;

import pl.s30953.mra.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repozytorium zapewniające podstawowe operacje bazodanowe dla encji User.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
