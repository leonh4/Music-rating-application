package pl.s30953.mra.dtos;

/**
 * DTO reprezentujący ocenę wydania muzycznego
 */
public record ReleaseDto (
        Integer id,
        String title,
        double averageRating
) {}
