package pl.s30953.mra.models;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/**
 * Konkretna implementacja artysty reprezentująca Zespół Muzyczny dziedziczy po abstrakcyjnej klasie Artist
 */
@Entity
public class Band extends Artist {
    @PastOrPresent(message = "Data założenia nie może być datą z przyszłości")
    private LocalDate dateOfFounding;

    private String placeOfFounding;

    public Band() {
    }

    public LocalDate getDateOfFounding() {
        return dateOfFounding;
    }

    public String getPlaceOfFounding() {
        return placeOfFounding;
    }

    public void setDateOfFounding(LocalDate dateOfFounding) {
        this.dateOfFounding = dateOfFounding;
    }

    public void setPlaceOfFounding(String placeOfFounding) {
        this.placeOfFounding = placeOfFounding;
    }
}
