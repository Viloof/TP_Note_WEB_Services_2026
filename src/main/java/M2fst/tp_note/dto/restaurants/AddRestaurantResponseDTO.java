package M2fst.tp_note.dto.restaurants;

import M2fst.tp_note.entities.RestaurantEntity;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddRestaurantResponseDTO
{
    public Long id;


    public static AddRestaurantResponseDTO fromEntity(RestaurantEntity restaurantEntity)
    {
        return AddRestaurantResponseDTO.builder()
                .id(restaurantEntity.getId())
                .build();
    }
}