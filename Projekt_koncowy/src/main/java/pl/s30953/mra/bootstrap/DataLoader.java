package pl.s30953.mra.bootstrap;

import jakarta.transaction.Transactional;
import pl.s30953.mra.models.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.s30953.mra.repositories.*;
import pl.s30953.mra.services.RankingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Komponent rozruchowy uruchamiany przy starcie aplikacji. Odpowiada za załadowanie początkowych danych testowych do
 * bazy SQLite.
 */
@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ReleaseRepository releaseRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final RankingService rankingService;

    public DataLoader(UserRepository userRepository, ReleaseRepository releaseRepository,
                      ArtistRepository artistRepository, GenreRepository genreRepository,
                      RankingService rankingService) {
        this.userRepository = userRepository;
        this.releaseRepository = releaseRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
        this.rankingService = rankingService;
    }

    /**
     * Metoda uruchomieniowa. Weryfikuje czy baza jest pusta, jeżeli tak to wywołuje metodę ładującą dane. Całość
     * działa w jednej transakcji, dzięki czemu wszystkie tworzone encje pozostają zarządzane do jej końca.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (releaseRepository.count() == 0) {
            loadData();
        }
    }

    /**
     * Tworzy i zapisuje obiekty użytkowników, artystów gatunków, wydań i ocen. Uruchamia także generowanie przykładowych
     * rankingów.
     * <p>
     * Po zapisaniu wydań artysta jest zapisywany ponownie, ponieważ to {@code Artist.releases} jest stroną
     * właścicielską asocjacji i dopiero ten zapis utrwala powiązania w tabeli {@code artist_release}.
     * <p>
     * Oceny są tworzone metodą {@link User#rateRelease(int, Release, String)}, która spina obie strony asocjacji
     * i przelicza średnią wydania. Nie trzeba ich zapisywać osobno: użytkownicy i wydania są obiektami zarządzanymi
     * w transakcji metody {@link #run(String...)}, więc oceny zapisują się kaskadowo przy jej zatwierdzeniu.
     */
    private void loadData() {
        User user1 = new User();
        user1.setUsername("Maciej1");
        user1.setEmail("MaciejJedynkowicz@example.com");
        user1.setPassword("haslo123456");
        user1.setStatus(UserStatus.ACTIVE);
        user1.setDateOfBirth(LocalDate.of(1995, 5, 15));

        User user2 = new User();
        user2.setUsername("Michal2");
        user2.setEmail("MichalDwojkowicz@example.com");
        user2.setPassword("123pass456");
        user2.setStatus(UserStatus.ACTIVE);
        user2.setDateOfBirth(LocalDate.of(1988, 11, 2));

        User user3 = new User();
        user3.setUsername("Anna3");
        user3.setEmail("AnnaTrojkowicz@example.com");
        user3.setPassword("hasl1231456");
        user3.setStatus(UserStatus.ACTIVE);
        user3.setDateOfBirth(LocalDate.of(1999, 3, 10));

        User user4 = new User();
        user4.setUsername("Krzysztof4");
        user4.setEmail("KrzysztofCzwartkowicz@example.com");
        user4.setPassword("krzysztof12345");
        user4.setStatus(UserStatus.ACTIVE);
        user4.setDateOfBirth(LocalDate.of(2001, 7, 22));


        userRepository.saveAll(List.of(user1, user2, user3, user4));


        Genre rock = new Genre();
        rock.setName("Rock");
        rock.setDescription("Typically uses a verse-chorus structure with a backbeat rhythm and the electric guitar at the forefront; generally heavier and/or faster than its predecessors.");
        genreRepository.save(rock);

        Band beatles = new Band();
        beatles.setName("The Beatles");
        beatles.setBiography("British rock band from Liverpool");
        beatles.setDateOfFounding(LocalDate.of(1960, 1, 1));
        beatles.setPlaceOfFounding("Liverpool, UK");
        artistRepository.save(beatles);

        Release abbeyRoad = createRelease("Abbey Road", LocalDate.of(1969, 9, 26), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.PSYCHEDELIC), rock, beatles);
        Release rubberSoul = createRelease("Rubber Soul", LocalDate.of(1965, 12, 3), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        releaseRepository.saveAll(List.of(abbeyRoad, rubberSoul));

        Release pleasePleaseMe = createRelease("Please Please Me", LocalDate.of(1963, 3, 22), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        Release withTheBeatles = createRelease("With the Beatles", LocalDate.of(1963, 11, 22), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        Release hardDaysNight = createRelease("A Hard Day's Night", LocalDate.of(1964, 7, 10), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        Release beatlesForSale = createRelease("Beatles for Sale", LocalDate.of(1964, 12, 4), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        Release help = createRelease("Help!", LocalDate.of(1965, 8, 6), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);
        Release revolver = createRelease("Revolver", LocalDate.of(1966, 8, 5), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.PSYCHEDELIC), rock, beatles);
        Release sgtPepper = createRelease("Sgt. Pepper's Lonely Hearts Club Band", LocalDate.of(1967, 5, 26), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.PSYCHEDELIC), rock, beatles);
        Release magicalMysteryTour = createRelease("Magical Mystery Tour", LocalDate.of(1967, 11, 27), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.PSYCHEDELIC), rock, beatles);
        Release whiteAlbum = createRelease("The Beatles (White Album)", LocalDate.of(1968, 11, 22), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK), rock, beatles);
        Release yellowSubmarine = createRelease("Yellow Submarine", LocalDate.of(1969, 1, 13), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.PSYCHEDELIC), rock, beatles);
        Release letItBe = createRelease("Let It Be", LocalDate.of(1970, 5, 8), ReleaseType.STUDIO_ALBUM, Set.of(Tag.ROCK, Tag.POP), rock, beatles);

        Release compilation1 = createRelease("1 (The Beatles Compilation)", LocalDate.of(2000, 11, 13), ReleaseType.COMPILATION, Set.of(Tag.ROCK, Tag.POP), rock, beatles);

        Release heyJude = createRelease("Hey Jude", LocalDate.of(1968, 8, 26), ReleaseType.SINGLE, Set.of(Tag.ROCK, Tag.POP), rock, beatles);

        releaseRepository.saveAll(List.of(
                pleasePleaseMe, withTheBeatles, hardDaysNight, beatlesForSale, help,
                revolver, sgtPepper, magicalMysteryTour, whiteAlbum, yellowSubmarine, letItBe,
                compilation1, heyJude
        ));

        artistRepository.save(beatles);

        user1.rateRelease(10, abbeyRoad, "nicee");
        user2.rateRelease(8, abbeyRoad, "aoao");
        user3.rateRelease(9, rubberSoul, "nice");
        user4.rateRelease(7, rubberSoul, "aaaaaaaaaaaaaaaaaaa");

        System.out.println("=".repeat(50));
        System.out.println("Added release: " + abbeyRoad.getTitle() + " avg. score: " + abbeyRoad.getAverageScore());
        System.out.println("Added release: " + rubberSoul.getTitle() + " avg. score: " + rubberSoul.getAverageScore());
        System.out.println("=".repeat(50));

        YearRanking yearRanking = new YearRanking();
        yearRanking.setYear(1969);

        rankingService.generateYearRanking(yearRanking);
        System.out.println("ranking: " + yearRanking.getYear() + " with " + yearRanking.getRankedReleases().size() + " releases");
        System.out.println("=".repeat(50));

        GenreRanking genreRanking =  new GenreRanking();
        genreRanking.setGenre(rock);

        rankingService.generateGenreRanking(genreRanking);
        System.out.println("ranking: " + genreRanking.getGenre().getName() + " with " + genreRanking.getRankedReleases().size() + " releases");
    }

    /**
     * Metoda pomocnicza służąca do generowania obiektów wydań muzycznych. Wydanie jest dopisywane do listy wydań
     * artysty, ponieważ {@code Artist.releases} jest stroną właścicielską asocjacji. Samo dodanie artysty do
     * {@code Release.artists} nie zostałoby zapisane w tabeli {@code artist_release}.
     *
     * @param title Tytuł wydania
     * @param date Data premiery
     * @param type Rodzaj nośnika
     * @param tags Zbiór tagów opisujących klimat wydania
     * @param genre Obiekt głównego gatunku muzycznego
     * @param artist Obiekt wykonawcy
     * @return Utworzony i powiązany asocjacjami obiekt Release
     */
    private Release createRelease(String title, LocalDate date, ReleaseType type, Set<Tag> tags, Genre genre, Band artist) {
        Release release = new Release();
        release.setTitle(title);
        release.setDateOfRelease(date);
        release.setReleaseType(type);
        release.setTags(tags);
        release.getGenres().add(genre);
        release.getArtists().add(artist);
        artist.getReleases().add(release);
        return release;
    }
}