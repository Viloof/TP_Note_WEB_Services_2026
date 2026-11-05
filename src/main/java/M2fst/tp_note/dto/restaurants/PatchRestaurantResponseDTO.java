package M2fst.tp_note.dto.restaurants;

import M2fst.tp_note.entities.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatchRestaurantResponseDTO
{
    private Long id;
    private String adresse;
    private String nom;

    public static PatchRestaurantResponseDTO fromEntity(RestaurantEntity restaurantEntity)
    {
        return PatchRestaurantResponseDTO.builder()
                .id(restaurantEntity.getId())
                .adresse(restaurantEntity.getAdresse())
                .nom(restaurantEntity.getNom())
                .build();
    }


}
