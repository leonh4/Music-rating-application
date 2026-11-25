package pl.s30953.mra.controllers;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.s30953.mra.dtos.RatingDto;
import pl.s30953.mra.dtos.ReleaseDto;
import pl.s30953.mra.services.ReleaseService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReleaseController.class)
class ReleaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReleaseService releaseService;

    /**
     * Lista wydań jest zwracana jako tablica JSON z kodem 200.
     */
    @Test
    void getAllReleasesReturnsJsonList() throws Exception {
        when(releaseService.getAllReleases()).thenReturn(List.of(
                new ReleaseDto(1, "Abbey Road", 9.0),
                new ReleaseDto(2, "Revolver", 0.0)));

        mockMvc.perform(get("/api/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Abbey Road"))
                .andExpect(jsonPath("$[0].averageRating").value(9.0))
                .andExpect(jsonPath("$[1].title").value("Revolver"));
    }

    /**
     * Brak wydań daje pustą tablicę z kodem 200.
     */
    @Test
    void getAllReleasesReturnsEmptyList() throws Exception {
        when(releaseService.getAllReleases()).thenReturn(List.of());

        mockMvc.perform(get("/api/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    /**
     * Oceny wydania są zwracane z datą w formacie ISO.
     */
    @Test
    void getRatingsForReleaseReturnsJsonList() throws Exception {
        when(releaseService.getRatingsForRelease(1)).thenReturn(List.of(
                new RatingDto(10, "nicee", "Maciej1", LocalDate.of(2024, 1, 15))));

        mockMvc.perform(get("/api/releases/1/ratings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].score").value(10))
                .andExpect(jsonPath("$[0].review").value("nicee"))
                .andExpect(jsonPath("$[0].username").value("Maciej1"))
                .andExpect(jsonPath("$[0].date").value("2024-01-15"));
    }

    /**
     * Brak wydania kończy się kodem 404.
     */
    @Test
    void getRatingsForMissingReleaseReturnsNotFound() throws Exception {
        when(releaseService.getRatingsForRelease(999)).thenThrow(new IllegalArgumentException("Release not found"));

        mockMvc.perform(get("/api/releases/999/ratings"))
                .andExpect(status().isNotFound());
    }

    /**
     * Inny błąd niż brak wydania nie jest zamieniany na 404, tylko przekazywany dalej.
     */
    @Test
    void getRatingsWithUnexpectedErrorIsNotTreatedAsNotFound() {
        when(releaseService.getRatingsForRelease(1)).thenThrow(new IllegalStateException("Database error"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/releases/1/ratings")))
                .isInstanceOf(ServletException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

    /**
     * Identyfikator, który nie jest liczbą, kończy się kodem 400.
     */
    @Test
    void getRatingsWithInvalidIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/releases/abc/ratings"))
                .andExpect(status().isBadRequest());
    }
}
