package pl.s30953.mra.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    /**
     * Tworzy wydanie o podanym identyfikatorze.
     */
    private static Release releaseWithId(int id) {
        Release release = new Release();
        release.setId(id);
        return release;
    }

    /**
     * Nowy użytkownik ma status UNVERIFIED.
     */
    @Test
    void newUserIsUnverified() {
        assertThat(new User().getStatus()).isEqualTo(UserStatus.UNVERIFIED);
    }

    /**
     * Potwierdzenie adresu e-mail aktywuje konto.
     */
    @Test
    void confirmEmailActivatesAccount() {
        User user = new User();

        user.confirmEmail();

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    /**
     * Bez daty urodzenia wiek wynosi -1.
     */
    @Test
    void calculateAgeWithoutDateOfBirthReturnsMinusOne() {
        assertThat(new User().calculateAge()).isEqualTo(-1);
    }

    /**
     * Wiek jest liczony w pełnych latach, gdy urodziny już w tym roku wypadły.
     */
    @Test
    void calculateAgeOnBirthday() {
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(20));

        assertThat(user.calculateAge()).isEqualTo(20);
    }

    /**
     * Wiek nie zwiększa się przed tegorocznymi urodzinami.
     */
    @Test
    void calculateAgeDayBeforeBirthday() {
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(20).plusDays(1));

        assertThat(user.calculateAge()).isEqualTo(19);
    }

    /**
     * Wystawienie oceny tworzy ocenę z pełnymi danymi i dopina ją do obu stron asocjacji.
     */
    @Test
    void rateReleaseLinksRatingToUserAndRelease() {
        User user = new User();
        Release release = new Release();

        user.rateRelease(9, release, "Świetny album");

        assertThat(user.getRatings()).hasSize(1);
        Rating rating = user.getRatings().get(0);
        assertThat(release.getRatings()).containsExactly(rating);
        assertThat(rating.getScore()).isEqualTo(9);
        assertThat(rating.getReview()).isEqualTo("Świetny album");
        assertThat(rating.getUser()).isSameAs(user);
        assertThat(rating.getRelease()).isSameAs(release);
        assertThat(rating.getDate()).isEqualTo(LocalDate.now());
    }

    /**
     * Ocena bez recenzji ma pustą recenzję.
     */
    @Test
    void rateReleaseWithoutReviewLeavesReviewEmpty() {
        User user = new User();
        Release release = new Release();

        user.rateRelease(7, release);

        assertThat(user.getRatings().get(0).getReview()).isNull();
    }

    /**
     * Każda nowa ocena od razu przelicza średnią wydania.
     */
    @Test
    void rateReleaseRecalculatesAverageScore() {
        User first = new User();
        User second = new User();
        Release release = new Release();

        first.rateRelease(10, release);
        assertThat(release.getAverageScore()).isEqualTo(10.0);

        second.rateRelease(7, release);
        assertThat(release.getAverageScore()).isEqualTo(8.5);
    }

    /**
     * Usunięcie oceny odpina ją od obu stron asocjacji i przelicza średnią.
     */
    @Test
    void deleteRatingUnlinksRatingAndRecalculatesAverage() {
        User first = new User();
        User second = new User();
        Release release = new Release();
        first.rateRelease(10, release);
        second.rateRelease(6, release);
        Rating rating = first.getRatings().get(0);

        first.deleteRating(rating);

        assertThat(first.getRatings()).isEmpty();
        assertThat(release.getRatings()).doesNotContain(rating);
        assertThat(release.getAverageScore()).isEqualTo(6.0);
        assertThat(rating.getUser()).isNull();
        assertThat(rating.getRelease()).isNull();
    }

    /**
     * Próba usunięcia cudzej oceny niczego nie zmienia.
     */
    @Test
    void deleteRatingOfAnotherUserDoesNothing() {
        User owner = new User();
        User other = new User();
        Release release = new Release();
        owner.rateRelease(8, release);
        Rating rating = owner.getRatings().get(0);

        other.deleteRating(rating);

        assertThat(owner.getRatings()).containsExactly(rating);
        assertThat(release.getRatings()).containsExactly(rating);
        assertThat(rating.getUser()).isSameAs(owner);
        assertThat(release.getAverageScore()).isEqualTo(8.0);
    }

    /**
     * Utworzenie listy z opisem ustawia wszystkie pola i właściciela.
     */
    @Test
    void createListWithDescription() {
        User user = new User();

        user.createList("Ulubione", "Najlepsze albumy");

        assertThat(user.getUsersLists()).hasSize(1);
        UsersList list = user.getUsersLists().get(0);
        assertThat(list.getTitle()).isEqualTo("Ulubione");
        assertThat(list.getDescription()).isEqualTo("Najlepsze albumy");
        assertThat(list.getDateOfCreation()).isEqualTo(LocalDate.now());
        assertThat(list.getUser()).isSameAs(user);
    }

    /**
     * Utworzenie listy bez opisu zostawia pusty opis.
     */
    @Test
    void createListWithoutDescription() {
        User user = new User();

        user.createList("Do przesłuchania");

        assertThat(user.getUsersLists().get(0).getDescription()).isNull();
    }

    /**
     * Dodanie wydania do kolekcji tworzy element z nośnikiem, datą i obiema stronami powiązania.
     */
    @Test
    void addToCollectionCreatesItem() {
        User user = new User();
        Release release = new Release();

        user.addToCollection(release, MediaType.VINYL);

        assertThat(user.getCollectionItems()).hasSize(1);
        CollectionItem item = user.getCollectionItems().get(0);
        assertThat(item.getRelease()).isSameAs(release);
        assertThat(item.getMediaType()).isEqualTo(MediaType.VINYL);
        assertThat(item.getAddedDate()).isEqualTo(LocalDate.now());
        assertThat(item.getUser()).isSameAs(user);
    }

    /**
     * Usunięcie z kolekcji działa także dla innej instancji tego samego wydania (wczytanej ponownie z bazy).
     */
    @Test
    void deleteFromCollectionMatchesReleaseById() {
        User user = new User();
        user.addToCollection(releaseWithId(1), MediaType.CD);
        user.addToCollection(releaseWithId(2), MediaType.DIGITAL);

        user.deleteFromCollection(releaseWithId(1));

        assertThat(user.getCollectionItems())
                .extracting(item -> item.getRelease().getId())
                .containsExactly(2);
    }

    /**
     * Usunięcie z kolekcji wydania, którego w niej nie ma, niczego nie zmienia.
     */
    @Test
    void deleteFromCollectionOfMissingReleaseDoesNothing() {
        User user = new User();
        user.addToCollection(releaseWithId(1), MediaType.CASSETTE);

        user.deleteFromCollection(releaseWithId(99));

        assertThat(user.getCollectionItems()).hasSize(1);
    }
}
