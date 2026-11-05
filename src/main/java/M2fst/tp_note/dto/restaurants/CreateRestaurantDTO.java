package M2fst.tp_note.dto.restaurants;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantDTO
{

    @Size(max = 90, message = "Le nom du restaurant ne peut pas dépasser 90 caractères.")
    @NotBlank(message = "Le nom du restaurant est obligatoire.")
    private String nom;
    @Size(max = 255,message = "L'adresse du restaurant ne peut pas dépasser 255 caractères.")
    @NotBlank(message = "L'adresse du restaurant est obligatoire.")
    private String adresse;
}
