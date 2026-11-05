package M2fst.tp_note.dto.evaluations;

import M2fst.tp_note.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateEvaluationResponseDTO
{

    private Long id;

    public static CreateEvaluationResponseDTO fromEntity(EvaluationEntity evaluationEntity)
    {
        return CreateEvaluationResponseDTO.builder()
                .id(evaluationEntity.getId())
                .build();
    }
}
