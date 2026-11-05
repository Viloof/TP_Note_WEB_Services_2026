package M2fst.tp_note.dto.evaluations;

import M2fst.tp_note.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PutEvaluationResponseDTO
{
    private Long id;
    private String commentaire;
    private int note;

    public static PutEvaluationResponseDTO fromEntity(EvaluationEntity evaluationEntity)
    {
        return PutEvaluationResponseDTO.builder()
                .id(evaluationEntity.getId())
                .commentaire(evaluationEntity.getContenu())
                .note(evaluationEntity.getNote())
                .build();
    }
}
