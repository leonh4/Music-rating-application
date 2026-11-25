package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Encja reprezentująca użytkownika systemu. Przechowuje dane profilowe, autoryzacyjne oraz zarządza powiązanymi listami,
 * ocenami i elementami kolekcji.
 */
@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters long")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    /** Asocjacja 1-do-wielu łącząca ocenę z wystawiającym ją użytkownikiem */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    /** Asocjacja 1-do-wielu łącząca elementy znajdujące się w kolekcji użytkownika */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionItem> collectionItems = new ArrayList<>();

    /** Asocjacja 1-do-wielu łącząca spersonalizowane listy z użytkownikiem, który je stworzył */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsersList> usersLists = new ArrayList<>();

    public User() {
        this.status = UserStatus.UNVERIFIED;
    }

    /**
     * Wylicza aktualny wiek użytkownika, realizując atrybut pochodny (/age).
     *
     * @return Wiek w latach lub -1 w przypadku braku daty urodzenia.
     */
    public int calculateAge() {
        if (this.dateOfBirth == null) {
            return -1;
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Metoda tworząca nową listę użytkownika, która spina relacje pomiędzy użytkownikiem a tworzoną listą.
     */
    public void createList(String title) {
        this.createList(title, null);
    }

    /**
     * Metoda tworząca nową listę użytkownika, która spina relacje pomiędzy użytkownikiem a tworzoną listą.
     */
    public void createList(String title, String description) {
        UsersList newList = new UsersList();
        newList.setTitle(title);
        newList.setDescription(description);
        newList.setDateOfCreation(LocalDate.now());
        newList.setUser(this);
        this.usersLists.add(newList);
    }

    /**
     * Wystawia ocenę (i opcjonalną recenzję) konkretnemu wydaniu. Wymusza synchronizację asocjacji oraz natychmiastowo
     * przelicza nową średnią ocenę wydania.
     */
    public void rateRelease(int score, Release release) {
        this.rateRelease(score, release, null);
    }

    /**
     * Wystawia ocenę (i opcjonalną recenzję) konkretnemu wydaniu. Wymusza synchronizację asocjacji oraz natychmiastowo
     * przelicza nową średnią ocenę wydania.
     */
    public void rateRelease(int score, Release release, String review) {
        Rating newRating = new Rating();
        newRating.setScore(score);
        newRating.setReview(review);
        newRating.setDate(LocalDate.now());
        newRating.setUser(this);
        newRating.setRelease(release);

        this.ratings.add(newRating);
        release.getRatings().add(newRating);

        release.calculateAverageScore();
    }

    /**
     * Usuwa ocenę użytkownika, dbając o integralność danych oraz wylicza nową średnią ocenę wydania.
     */
    public void deleteRating(Rating rating) {
        if (this.ratings.contains(rating)) {
            this.ratings.remove(rating);

            Release release = rating.getRelease();
            if (release != null) {
                release.getRatings().remove(rating);
                release.calculateAverageScore();
            }
            rating.setUser(null);
            rating.setRelease(null);
        }
    }

    /**
     * Dodaje wydanie do kolekcji użytkownika, wymuszając podanie typu nośnika oraz wiążąc użytkownika z danym wydaniem.
     */
    public void addToCollection(Release release, MediaType mediaType) {
        CollectionItem item = new CollectionItem();
        item.setRelease(release);
        item.setMediaType(mediaType);
        item.setAddedDate(LocalDate.now());
        item.setUser(this);
        this.collectionItems.add(item);
    }

    /**
     * Usuwa wydanie z kolekcji użytkownika, jeżeli podane wydanie znajduje się w kolekcji użytkownika.
     */
    public void deleteFromCollection(Release release) {
        this.collectionItems.removeIf(item -> item.getRelease().equals(release));
    }

    /**
     * Zmienia status konta na aktywny
     */
    public void confirmEmail() {
        this.status = UserStatus.ACTIVE;
    }

    public void deleteAccount() {

    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public  String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public List<CollectionItem> getCollectionItems() {
        return collectionItems;
    }

    public List<UsersList> getUsersLists() {
        return usersLists;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername( String username) {
        this.username = username;
    }

    public void setEmail( String email) {
        this.email = email;
    }

    public void setPassword( String password) {
        this.password = password;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDateOfBirth( LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public void setCollectionItems(List<CollectionItem> collectionItems) {
        this.collectionItems = collectionItems;
    }

    public void setUsersLists(List<UsersList> usersLists) {
        this.usersLists = usersLists;
    }
}
