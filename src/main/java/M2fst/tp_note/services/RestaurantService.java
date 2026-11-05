package M2fst.tp_note.services;

import M2fst.tp_note.dto.restaurants.RestaurantDTO;
import M2fst.tp_note.dto.evaluations.EvaluationDTO;
import M2fst.tp_note.entities.EvaluationEntity;
import M2fst.tp_note.entities.RestaurantEntity;
import M2fst.tp_note.exceptions.BadRequestException;
import M2fst.tp_note.exceptions.InternalServerError;
import M2fst.tp_note.exceptions.NotFoundException;
import M2fst.tp_note.repositories.EvaluationRepository;
import M2fst.tp_note.repositories.RestaurantRepository;
import M2fst.tp_note.repositories.S3Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService
{
    private final RestaurantRepository restaurantRepository;
    private final EvaluationRepository evaluationRepository;
    private final S3Repository s3Repository;

    // Méthode pour créer un restaurant, cette méthode prend en paramètre le nom et l'adresse du restaurant à créer.
    public RestaurantEntity createRestaurant(String name, String adresse)
    {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setNom(name);
        restaurantEntity.setAdresse(adresse);
        restaurantRepository.save(restaurantEntity);
        return restaurantEntity;
    }

    // Méthode pour mettre à jour les informations d'un restaurant, cette méthode prend en paramètre le nom, l'adresse et l'id du restaurant à mettre à jour.
    // Si le nom ou l'adresse est null, ils ne seront pas mis à jour.
    public RestaurantEntity updateRestaurant(String name, String address, Long id)
    {
        RestaurantEntity restaurantEntity = getRestaurantById(id);

        if (!name.isBlank())
            restaurantEntity.setNom(name);
        if (!address.isBlank())
            restaurantEntity.setAdresse(address);

        restaurantRepository.save(restaurantEntity);
        return restaurantEntity;

    }

    // Méthode pour ajouter une URL d'image à un restaurant
    public String addImageUrl(Long id, String imageUrl)
    {
        RestaurantEntity restaurantEntity = getRestaurantById(id);
        String url = imageUrl + "_letts_restoImg.jpg";
        restaurantEntity.setImageUrl(url);
        restaurantRepository.save(restaurantEntity);
        return url;
    }


    // Méthode pour récupérer tous les restaurants sous forme d'une collection de DTO
    public Collection<RestaurantDTO> getRestaurants()
    {
        return restaurantRepository.findAll().stream().map(restaurantEntity -> RestaurantDTO.builder()
                    .nom(restaurantEntity.getNom())
                    .adresse(restaurantEntity.getAdresse())
                    .id(restaurantEntity.getId())
                    .imageUrl(restaurantEntity.getImageUrl())
                    .noteMoyenne(this.getNoteMoyenne(restaurantEntity.getId()))
                    .build())
                .toList();
    }

    // Méthode pour récupérer un restaurant par son ID
    public RestaurantEntity getRestaurant(Long id)
    {
        return getRestaurantById(id);
    }

    // Méthode pour supprimer un restaurant par son ID
    public RestaurantEntity deleteRestaurant(Long id)
    {
        RestaurantEntity restaurant = getRestaurantById(id);

        List<EvaluationEntity> evaluations =
                evaluationRepository.findAllByRestaurant_Id(id);

        List<String> imageKeys = evaluations.stream()
                .filter(evaluation -> evaluation.getImages() != null)
                .flatMap(evaluation -> evaluation.getImages().stream())
                .filter(imageKey -> imageKey != null && !imageKey.isBlank())
                .toList();

        s3Repository.deleteEvalImages(imageKeys);
        restaurantRepository.delete(restaurant);

        return restaurant;
    }

    // Méthode pour calculer la note moyenne d'un restaurant à partir de ses évaluations
    public final float getNoteMoyenne(Long id)
    {
        List<EvaluationEntity> evaluations = evaluationRepository.findAllByRestaurant_Id(id);

        if (evaluations.isEmpty())
            return -1;

        float sum = 0;

        for (EvaluationEntity evaluation : evaluations)
            sum += evaluation.getNote();


        return sum / evaluations.size();
    }

    // Méthode privée pour récupérer un restaurant par son ID, avec gestion de l'exception si le restaurant n'est pas trouvé
    private RestaurantEntity getRestaurantById(Long id)
    {
        return restaurantRepository.findById(Math.toIntExact(id)).orElseThrow(() -> new NotFoundException("Restaurant avec l'id " + id + " non trouvé."));
    }

}
