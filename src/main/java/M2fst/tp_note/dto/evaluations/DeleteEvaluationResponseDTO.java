package M2fst.tp_note.dto.evaluations;

import M2fst.tp_note.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeleteEvaluationResponseDTO
{
    private String commentaire;

    public static DeleteEvaluationResponseDTO fromEntity(EvaluationEntity evaluation)
    {
        return DeleteEvaluationResponseDTO.builder()
                .commentaire(evaluation.getContenu())
                .build();
    }
}
