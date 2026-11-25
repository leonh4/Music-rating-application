package pl.s30953.mra.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import pl.s30953.mra.models.GenreRanking;
import pl.s30953.mra.models.Release;
import pl.s30953.mra.models.YearRanking;
import pl.s30953.mra.repositories.RankingRepository;
import pl.s30953.mra.repositories.ReleaseRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Serwis odpowiedzialny za wyliczania i utrwalania rankingów wydań muzycznych.
 */
@Service
public class RankingService {
    private final ReleaseRepository releaseRepository;
    private final RankingRepository rankingRepository;


    public RankingService(ReleaseRepository releaseRepository, RankingRepository rankingRepository) {
        this.releaseRepository = releaseRepository;
        this.rankingRepository = rankingRepository;
    }

    /**
     * Generuje i zapisuje do bazy ranking wydań muzycznych z konkretnego roku kalendarzowego.
     *
     * @param yearRanking Inicjalny obiekt rankingu z ustawionym rokiem docelowym.
     */
    @Transactional
    public void generateYearRanking(YearRanking yearRanking) {
        yearRanking.generateRanking();

        LocalDate startDate = LocalDate.of(yearRanking.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(yearRanking.getYear(), 12, 31);

        List<Release> topReleases = releaseRepository.findTopReleasesByYear(startDate, endDate,
                yearRanking.getMinimumScore(), yearRanking.getMinimumOfRatings());

        yearRanking.setRankedReleases(topReleases);

        for (Release r : topReleases) {
            if (!r.getRankings().contains(yearRanking)) {
                r.getRankings().add(yearRanking);
            }
        }

        rankingRepository.save(yearRanking);
    }

    /**
     * Generuje i zapisuje do bazy ranking wydań reprezentujących dany gatunek muzyczny.
     *
     * @param genreRanking Inicjalny obiekt rankingu powiązany z obiektem Genre
     */
    @Transactional
    public void generateGenreRanking(GenreRanking genreRanking) {
        genreRanking.generateRanking();

        List<Release> topReleases = releaseRepository.findTopReleasesByGenre(genreRanking.getGenre().getName(),
                genreRanking.getMinimumScore(), genreRanking.getMinimumOfRatings());

        for (Release r : topReleases) {
            if (!r.getRankings().contains(genreRanking)) {
                r.getRankings().add(genreRanking);
            }
        }

        genreRanking.setRankedReleases(topReleases);
        rankingRepository.save(genreRanking);
    }
}
