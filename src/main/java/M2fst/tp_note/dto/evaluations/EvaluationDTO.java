package M2fst.tp_note.dto.evaluations;

import M2fst.tp_note.entities.EvaluationEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Builder
public class EvaluationDTO
{
    private String contenu;
    private Long id;
    private String auteur;
    private int note;
    private Collection<String> images;
    private Long restaurantId;



    public static EvaluationDTO fromEntity(EvaluationEntity evaluationEntity)
    {
        return EvaluationDTO.builder()
                .id(evaluationEntity.getId())
                .contenu(evaluationEntity.getContenu())
                .auteur(evaluationEntity.getAuteur())
                .note(evaluationEntity.getNote())
                .images(evaluationEntity.getImages())
                .restaurantId(evaluationEntity.getRestaurant().getId())
                .build();
    }

}
