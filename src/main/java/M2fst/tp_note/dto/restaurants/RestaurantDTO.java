package M2fst.tp_note.dto.restaurants;

import M2fst.tp_note.entities.RestaurantEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RestaurantDTO
{
    @NotBlank
    @Size(max = 90)
    private String nom;

    @NotBlank
    @PositiveOrZero
    private Long id;

    @Size(max = 255)
    private String adresse;

    private String imageUrl;

    private Float noteMoyenne;

    public static RestaurantDTO fromEntity(RestaurantEntity restaurantEntity, float noteMoyenne)
    {
        return RestaurantDTO.builder()
                .id(restaurantEntity.getId())
                .nom(restaurantEntity.getNom())
                .adresse(restaurantEntity.getAdresse())
                .imageUrl(restaurantEntity.getImageUrl())
                .noteMoyenne(noteMoyenne)
                .build();
    }

}
