package M2fst.tp_note.repositories;

import M2fst.tp_note.entities.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Integer>
{

    List<EvaluationEntity> findAllByAuteur(String auteur);

    List<EvaluationEntity> findAllByRestaurant_Id(Long restaurantId);
}
