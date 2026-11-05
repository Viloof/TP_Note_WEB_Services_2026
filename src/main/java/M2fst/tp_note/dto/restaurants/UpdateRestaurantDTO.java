package M2fst.tp_note.dto.restaurants;

import M2fst.tp_note.exceptions.BadRequestException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantDTO
{
    @Size(max = 90, message = "Nom must be strictly less than 91 characters")
    private String nom;
    @Size(max = 255, message = "Adresse must be strictly less than 256 characters")
    private String adresse;

}
