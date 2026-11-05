package M2fst.tp_note.services;

import M2fst.tp_note.dto.evaluations.EvaluationDTO;
import M2fst.tp_note.entities.EvaluationEntity;
import M2fst.tp_note.entities.RestaurantEntity;
import M2fst.tp_note.exceptions.InternalServerError;
import M2fst.tp_note.exceptions.NotFoundException;
import M2fst.tp_note.repositories.EvaluationRepository;
import M2fst.tp_note.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService
{
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;

    public Long getNextId()
    {
        return evaluationRepository.findAll().stream().mapToLong(EvaluationEntity::getId).max().orElse(0L) + 1;
    }


    public EvaluationEntity createEvaluation(String contenu, Long id, Integer note, String author)
    {
        RestaurantEntity restaurant = restaurantRepository.findAll()
                .stream()
                .filter(restaurantEntity -> restaurantEntity.getId().equals(id)).findFirst().orElse(null);

        if (restaurant == null)
        {
            throw new NotFoundException("Restaurant avec l'id " + id + " n'existe pas.");
        }

        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setContenu(contenu);
        evaluation.setRestaurant(restaurant);
        evaluation.setNote(note);
        evaluation.setAuteur(author);
        evaluationRepository.save(evaluation);
        return evaluation;
    }


    public EvaluationEntity updateEvaluation(Long id, String contenu, Integer note)
    {
        EvaluationEntity evaluation = getEvaluationById(id);
        evaluation.setContenu(contenu);
        evaluation.setNote(note);
        evaluationRepository.save(evaluation);
        return evaluation;
    }


    public Collection<EvaluationDTO> getAllEvaluations()
    {
        return evaluationRepository.findAll().stream().map(evaluation -> EvaluationDTO.builder()
                .contenu(evaluation.getContenu())
                .note(evaluation.getNote())
                .auteur(evaluation.getAuteur())
                .id(evaluation.getId())
                .images(evaluation.getImages())
                .restaurantId(evaluation.getRestaurant().getId())
                .build()).toList();
    }


    public Collection<EvaluationDTO> getEvaluationsByUser(String username)
    {
        List<EvaluationEntity> evaluations = evaluationRepository.findAllByAuteur(username);

        return evaluations.stream().map(evaluation -> EvaluationDTO.builder()
                .contenu(evaluation.getContenu())
                .auteur(evaluation.getAuteur())
                .id(evaluation.getId())
                .note(evaluation.getNote())
                .images(evaluation.getImages())
                .restaurantId(evaluation.getRestaurant().getId())
                .build()).toList();
    }


    public Collection<EvaluationDTO> getRestaurantEvaluationsByUser(Long id, String author)
    {
        RestaurantEntity restaurant = restaurantRepository.findById(Math.toIntExact(id)).orElse(null);
        if (restaurant == null)
            throw new NotFoundException("Restaurant avec l'id " + id + " n'existe pas.");

        List<EvaluationEntity> evaluations = evaluationRepository.findAll()
                .stream()
                .filter(evaluation -> evaluation.getRestaurant().getId().equals(id)).toList();

        List<EvaluationEntity> evaluationsByAuthor = evaluations.stream()
                .filter(evaluation -> evaluation.getAuteur().equals(author)).toList();

        return evaluationsByAuthor.stream().map(evaluation -> EvaluationDTO.builder()
                .contenu(evaluation.getContenu())
                .auteur(evaluation.getAuteur())
                .id(evaluation.getId())
                .note(evaluation.getNote())
                .images(evaluation.getImages())
                .restaurantId(evaluation.getRestaurant().getId())
                .build()).toList();
    }


    public EvaluationEntity deleteEvaluation(Long id)
    {
        EvaluationEntity evaluation = getEvaluationById(id);
        evaluationRepository.delete(evaluation);
        return evaluation;
    }


    private EvaluationEntity getEvaluationById(Long id)
    {
        return evaluationRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new NotFoundException("Evaluation avec l'id " + id + " non trouvé."));
    }


    public List<String> addImageUrl(Long id, int nbFiles)
    {
        EvaluationEntity evaluationEntity = getEvaluationById(id);

        try
        {
            List<String> images = new ArrayList<>();

            for (int i = 0; i < nbFiles; i++)
                images.add(id + "." + i + "_letts_evalImg.jpg");

            evaluationEntity.setImages(images);
            evaluationRepository.save(evaluationEntity);
            return images;
        }
        catch (Exception e)
        {
            throw new InternalServerError("Erreur lors de l'ajout des images à l'évaluation avec l'id "+ id + ": " + e.getMessage());
        }
    }


    public Collection<String> getImageUrls(Long id)
    {
        EvaluationEntity evaluationEntity = getEvaluationById(id);
        return evaluationEntity.getImages();
    }


    public Collection<EvaluationDTO> getRestaurantEvaluations(Long aLong)
    {
        RestaurantEntity resto = restaurantRepository.findById(Math.toIntExact(aLong)).orElseThrow(() -> new NotFoundException("Restaurant avec l'id " + aLong + " non trouvé."));

        List<EvaluationEntity> evaluations = evaluationRepository.findAll()
                .stream()
                .filter(evaluation -> evaluation.getRestaurant().getId().equals(resto.getId())).toList();

        return evaluations.stream().map(evaluation -> EvaluationDTO.builder()
                .contenu(evaluation.getContenu())
                .auteur(evaluation.getAuteur())
                .id(evaluation.getId())
                .note(evaluation.getNote())
                .images(evaluation.getImages())
                .restaurantId(evaluation.getRestaurant().getId())
                .build()).toList();
    }
}
