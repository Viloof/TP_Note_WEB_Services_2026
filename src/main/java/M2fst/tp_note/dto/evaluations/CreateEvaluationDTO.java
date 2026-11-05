package M2fst.tp_note.dto.evaluations;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

// DTO pour la création d'une évaluation
public class CreateEvaluationDTO
{

    @Min(value = 0, message = "La note doit être supérieure ou égale à 0.")
    @Max(value = 3, message = "La note doit être inférieure ou égale à 3.")
    private int note;

    @Size(max = 255,message = "Le commentaire ne peut pas dépasser 255 caractères.")
    @NotBlank(message = "Le commentaire est obligatoire.")
    private String commentaire;
}
