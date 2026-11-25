package pl.s30953.mra.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * Konkretna implementacja artysty reprezentująca Muzyka Solowego. Dziedziczy po abstrakcyjnej klasie Artist
 */
@Entity
public class SoloArtist extends Artist {
    @NotBlank(message = "Imię artysty solowego nie może być puste")
    private String firstName;

    @NotBlank(message = "Nazwisko artysty solowego nie może być puste")
    private String lastName;

    @PastOrPresent(message = "Data urodzenia nie może być datą z przyszłości")
    private LocalDate dateOfBirth;

    private String placeOfBirth;

    public SoloArtist() {}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }
}
