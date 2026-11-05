package M2fst.tp_note.dto.restaurants;

import M2fst.tp_note.entities.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeleteRestaurantResponseDTO
{
    public String nom;


    public static DeleteRestaurantResponseDTO fromEntity(RestaurantEntity restaurantEntity) {
        return DeleteRestaurantResponseDTO.builder()
                .nom(restaurantEntity.getNom())
                .build();
    }
}
