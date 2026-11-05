package M2fst.tp_note.dto.evaluations;

import M2fst.tp_note.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PatchEvaluationResponseDTO
{
    private Long id;
    private String commentaire;
    private int note;

    public static PatchEvaluationResponseDTO fromEntity(EvaluationEntity evaluationEntity)
    {
        return PatchEvaluationResponseDTO.builder()
                .id(evaluationEntity.getId())
                .commentaire(evaluationEntity.getContenu())
                .note(evaluationEntity.getNote())
                .build();
    }
}
