package pl.s30953.mra.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje spersonalizowaną listę wydań muzycznych stworzoną przez użytkownika. Na przykład "Ulubione".
 */
@Entity
public class UsersList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "title cannot be blank")
    @Column(nullable = false)
    private String title;

    private String description;

    @NotNull()
    private LocalDate dateOfCreation;

    /** Asocjacja 1-do-wielu łącząca listę z użytkownikiem, który jest właścicielem listy */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Asocjacja wiele-do-wielu łącząca wydania muzyczne wchodzące w skład listy */
    @ManyToMany
    @JoinTable(
            name = "users_list_release",
            joinColumns = @JoinColumn(name = "users_list_id"),
            inverseJoinColumns = @JoinColumn(name = "release_id")
    )
    private List<Release> releases = new ArrayList<>();

    public UsersList() {}

    /**
     * Dodaje pojedyncze wydanie do listy, zapobiegając duplikatom.
     */
    public void addReleaseToList(Release release) {
        if(!this.releases.contains(release)) {
            this.releases.add(release);
        }
    }

    /**
     * Dodaje wiele wydań do listy na raz, zapobiegając duplikatom
     */
    public void addReleasesToList(List<Release> releases) {
        for(Release release : releases) {
            if(!this.releases.contains(release)) {
                this.releases.add(release);
            }
        }
    }

    public void removeReleaseFromList(Release release) {
        this.releases.remove(release);
    }

    public void removeReleasesFromList(List<Release> releases) {
        this.releases.removeAll(releases);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }

    public User getUser() {
        return user;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
