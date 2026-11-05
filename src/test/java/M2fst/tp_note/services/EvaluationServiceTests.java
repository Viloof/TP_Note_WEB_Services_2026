package M2fst.tp_note.services;

import M2fst.tp_note.dto.evaluations.EvaluationDTO;
import M2fst.tp_note.entities.EvaluationEntity;
import M2fst.tp_note.entities.RestaurantEntity;
import M2fst.tp_note.exceptions.NotFoundException;
import M2fst.tp_note.repositories.EvaluationRepository;
import M2fst.tp_note.repositories.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTests
{

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    // -------------------------------Test de la méthode getNextId----------------------------------
    @Test
    void testGetNextIdWhenNoEvaluations()
    {
        when(evaluationRepository.findAll()).thenReturn(List.of());

        Long nextId = evaluationService.getNextId();

        assertThat(nextId).isEqualTo(1L);
        verify(evaluationRepository).findAll();

    }

    @Test
    void testGetNextIdWithExistingEvaluations()
    {
        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setId(1L);

        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setId(4L);

        when(evaluationRepository.findAll()).thenReturn(List.of(evaluation1, evaluation2));

        Long nextId = evaluationService.getNextId();

        assertThat(nextId).isEqualTo(5L);
        verify(evaluationRepository).findAll();
    }

    // -------------------------------Test de la méthode createEvaluation----------------------------------
    @Test
    void testCreateEvaluation()
    {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1L);

        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        EvaluationEntity evaluation = evaluationService.createEvaluation("Top moumoute", restaurant.getId(), 3, "super testeur");

        assertThat(evaluation.getContenu()).isEqualTo("Top moumoute");
        assertThat(evaluation.getRestaurant()).isEqualTo(restaurant);
        assertThat(evaluation.getNote()).isEqualTo(3);
        assertThat(evaluation.getAuteur()).isEqualTo("super testeur");

        verify(evaluationRepository).save(evaluation);
    }

    @Test
    void testCreateEvaluationRestaurantNotFound()
    {
        when(restaurantRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> evaluationService.createEvaluation("Top moumoute", 1L, 3, "super testeur")).isInstanceOf(NotFoundException.class).hasMessageContaining("Restaurant avec l'id 1 n'existe pas.");
    }

    // -------------------------------Test de la méthode updateEvaluation----------------------------------
    @Test
    void testUpdateEvaluation()
    {
        long evaluationId = 1L;
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(evaluationId);

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.of(evaluation));

        EvaluationEntity updatedEvaluation = evaluationService.updateEvaluation(evaluationId, "Nouveau contenu", 1);

        assertThat(updatedEvaluation.getContenu()).isEqualTo("Nouveau contenu");
        assertThat(updatedEvaluation.getNote()).isEqualTo(1);

        verify(evaluationRepository).save(updatedEvaluation);
    }

    @Test
    void testUpdateEvaluationNotFound()
    {
        long evaluationId = 1L;

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.updateEvaluation(evaluationId, "Nouveau contenu", 4)).isInstanceOf(NotFoundException.class).hasMessageContaining("Evaluation avec l'id " + evaluationId + " non trouvé.");
    }

    // -------------------------------Test de la méthode getAllEvaluations----------------------------------
    @Test
    void testGetAllEvaluations()
    {
        long evaluationId1 = 1L;
        long evaluationId2 = 2L;
        long restaurantId1 = 1L;
        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setId(evaluationId1);
        evaluation1.setRestaurant(new RestaurantEntity());
        evaluation1.getRestaurant().setId(restaurantId1);
        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setId(evaluationId2);
        evaluation2.setRestaurant(new RestaurantEntity());
        evaluation2.getRestaurant().setId(restaurantId1);

        when(evaluationRepository.findAll()).thenReturn(List.of(evaluation1, evaluation2));

        assertThat(evaluationService.getAllEvaluations()).hasSize(2);
        verify(evaluationRepository).findAll();
    }

    // -------------------------------Test de la méthode getEvaluationsByUser----------------------------------
    @Test
    void testGetEvaluationsByUser()
    {
        long evaluationId1 = 1L;
        long evaluationId2 = 2L;
        long evaluationId3 = 3L;
        long restaurantId1 = 1L;
        String username = "testUser";
        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setId(evaluationId1);
        evaluation1.setRestaurant(new RestaurantEntity());
        evaluation1.getRestaurant().setId(restaurantId1);
        evaluation1.setAuteur(username);
        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setId(evaluationId2);
        evaluation2.setRestaurant(new RestaurantEntity());
        evaluation2.getRestaurant().setId(restaurantId1);
        evaluation2.setAuteur(username);
        EvaluationEntity evaluation3 = new EvaluationEntity();
        evaluation3.setId(evaluationId3);
        evaluation3.setRestaurant(new RestaurantEntity());
        evaluation3.getRestaurant().setId(restaurantId1);
        evaluation3.setAuteur("anotherUser");

        when(evaluationRepository.findAllByAuteur(username)).thenReturn(List.of(evaluation1, evaluation2));

        assertThat(evaluationService.getEvaluationsByUser(username)).hasSize(2);
        verify(evaluationRepository).findAllByAuteur(username);
    }


    // -------------------------------Test de la méthode getRestaurantEvaluationsByUser----------------------------------
    @Test
    void testGetRestaurantEvaluationsByUser()
    {
        long evaluationId1 = 1L;
        long evaluationId2 = 2L;
        long evaluationId3 = 3L;
        long restaurantId1 = 1L;
        String username = "testUser";
        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setId(evaluationId1);
        evaluation1.setRestaurant(new RestaurantEntity());
        evaluation1.getRestaurant().setId(restaurantId1);
        evaluation1.setAuteur(username);
        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setId(evaluationId2);
        evaluation2.setRestaurant(new RestaurantEntity());
        evaluation2.getRestaurant().setId(restaurantId1);
        evaluation2.setAuteur(username);
        EvaluationEntity evaluation3 = new EvaluationEntity();
        evaluation3.setId(evaluationId3);
        evaluation3.setRestaurant(new RestaurantEntity());
        evaluation3.getRestaurant().setId(restaurantId1);
        evaluation3.setAuteur("anotherUser");

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId1);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(evaluationRepository.findAll()).thenReturn(List.of(evaluation1, evaluation2, evaluation3));

        assertThat(evaluationService.getRestaurantEvaluationsByUser(restaurantId1, username)).hasSize(2);
        verify(restaurantRepository).findById(Math.toIntExact(restaurantId1));
        verify(evaluationRepository).findAll();
    }

    @Test
    void testGetRestaurantEvaluationsByUserRestaurantNotFound()
    {
        long restaurantId1 = 1L;
        String username = "testUser";

        assertThatThrownBy(() -> evaluationService.getRestaurantEvaluationsByUser(restaurantId1, username)).isInstanceOf(NotFoundException.class).hasMessageContaining("Restaurant avec l'id " + restaurantId1 + " n'existe pas.");
    }

    // -------------------------------Test de la méthode deleteEvaluation----------------------------------
    @Test
    void testDeleteEvaluation()
    {
        long evaluationId = 1L;
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(evaluationId);

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.of(evaluation));

        EvaluationEntity deletedEvaluation = evaluationService.deleteEvaluation(evaluationId);

        assertThat(deletedEvaluation).isEqualTo(evaluation);
        verify(evaluationRepository).delete(evaluation);
    }

    @Test
    void testDeleteEvaluationNotFound()
    {
        long evaluationId = 1L;

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.deleteEvaluation(evaluationId)).isInstanceOf(NotFoundException.class).hasMessageContaining("Evaluation avec l'id " + evaluationId + " non trouvé.");
    }

    // -------------------------------Test de la méthode addImageUrl----------------------------------
    @Test
    void testAddImageUrl()
    {
        long evaluationId = 1L;
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(evaluationId);

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.of(evaluation));

        List<String> imageUrls = evaluationService.addImageUrl(evaluationId, 3);

        assertThat(imageUrls).hasSize(3);
        assertThat(imageUrls.get(0)).isEqualTo( evaluationId + "." + "0" + "_letts_evalImg.jpg");
        assertThat(imageUrls.get(1)).isEqualTo( evaluationId + "." + "1" + "_letts_evalImg.jpg");
        assertThat(imageUrls.get(2)).isEqualTo( evaluationId + "." + "2" + "_letts_evalImg.jpg");

        verify(evaluationRepository).save(evaluation);


    }

    @Test
    void testAddImageUrlEvaluationNotFound()
    {
        long evaluationId = 1L;

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.addImageUrl(evaluationId, 3)).isInstanceOf(NotFoundException.class).hasMessageContaining("Evaluation avec l'id " + evaluationId + " non trouvé.");
    }

    // -------------------------------Test de la méthode getImageUrls----------------------------------
    @Test
    void testGetImageUrls()
    {
        long evaluationId = 1L;
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(evaluationId);
        evaluation.setImages(List.of("image1.jpg", "image2.jpg"));

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.of(evaluation));

        List<String> imageUrls = (List<String>) evaluationService.getImageUrls(evaluationId);

        assertThat(imageUrls).hasSize(2);
        assertThat(imageUrls.get(0)).isEqualTo("image1.jpg");
        assertThat(imageUrls.get(1)).isEqualTo("image2.jpg");
    }

    @Test
    void testGetImageUrlsEvaluationNotFound()
    {
        long evaluationId = 1L;

        when(evaluationRepository.findById((int) evaluationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.getImageUrls(evaluationId)).isInstanceOf(NotFoundException.class).hasMessageContaining("Evaluation avec l'id " + evaluationId + " non trouvé.");
    }

    // -------------------------------Test de la méthode getRestaurantEvaluations----------------------------------
    @Test
    void testGetRestaurantEvaluations()
    {
        long restaurantId = 1L;
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);

        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setId(1L);
        evaluation1.setRestaurant(restaurant);

        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setId(2L);
        evaluation2.setRestaurant(restaurant);

        when(restaurantRepository.findById((int) restaurantId)).thenReturn(Optional.of(restaurant));
        when(evaluationRepository.findAll()).thenReturn(List.of(evaluation1, evaluation2));

        List<EvaluationDTO> evaluations = (List<EvaluationDTO>) evaluationService.getRestaurantEvaluations(restaurantId);

        assertThat(evaluations).hasSize(2);
        assertThat(evaluations.get(0).getId()).isEqualTo(1L);
        assertThat(evaluations.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void testGetRestaurantEvaluationsRestaurantNotFound()
    {
        long restaurantId = 1L;

        when(restaurantRepository.findById((int) restaurantId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.getRestaurantEvaluations(restaurantId)).isInstanceOf(NotFoundException.class).hasMessageContaining("Restaurant avec l'id " + restaurantId + " non trouvé.");
    }
}