package pl.s30953.mra.dtos;

import java.time.LocalDate;

/**
 * DTO reprezentujący skrócone informacje o wydaniu muzycznym
 */
public record RatingDto(
    Integer score,
    String review,
    String username,
    LocalDate date
) { }
