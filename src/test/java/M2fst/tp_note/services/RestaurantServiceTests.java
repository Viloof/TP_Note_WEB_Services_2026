package M2fst.tp_note.services;

import M2fst.tp_note.entities.EvaluationEntity;
import M2fst.tp_note.entities.RestaurantEntity;
import M2fst.tp_note.exceptions.NotFoundException;
import M2fst.tp_note.repositories.EvaluationRepository;
import M2fst.tp_note.repositories.RestaurantRepository;
import M2fst.tp_note.repositories.S3Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTests
{

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private S3Repository s3Repository;


    // -------------------------------Test de la méthode createRestaurant----------------------------------
    @Test
    void testCreateRestaurant()
    {
        String name = "Nouveau restaurant";
        String adresse = "Nouvelle adresse";

        RestaurantEntity createdRestaurant = restaurantService.createRestaurant(name, adresse);

        assertThat(createdRestaurant.getNom()).isEqualTo(name);
        assertThat(createdRestaurant.getAdresse()).isEqualTo(adresse);

        verify(restaurantRepository).save(createdRestaurant);
    }

    // -------------------------------Test de la méthode updateRestaurant----------------------------------
    @Test
    void testUpdateRestaurantName() {

        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurant("TestChange","", restaurantId);

        assertThat(updatedRestaurant.getNom()).isEqualTo("TestChange");
        assertThat(updatedRestaurant.getAdresse()).isEqualTo("Ancienne adresse");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testUpdateRestaurantAdresse()
    {
        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurant("","Nouvelle adresse", restaurantId);

        assertThat(updatedRestaurant.getAdresse()).isEqualTo("Nouvelle adresse");
        assertThat(updatedRestaurant.getNom()).isEqualTo("Test RestaurantDTO");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testUpdateRestaurantNameAndAdresse()
    {
        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurant("TestChange","Nouvelle adresse", restaurantId);

        assertThat(updatedRestaurant.getNom()).isEqualTo("TestChange");
        assertThat(updatedRestaurant.getAdresse()).isEqualTo("Nouvelle adresse");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
        verify(restaurantRepository).save(restaurant);
    }


    @Test
    void testUpdateRestaurantNotFound()
    {
        long restaurantId = 1L;
        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> restaurantService.updateRestaurant("TestChange", "Nouvelle adresse", restaurantId));
        assertThat(exception.getMessage()).isEqualTo("Restaurant avec l'id 1 non trouvé.");
        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
    }

    @Test
    void testUpdateRestaurantNoChanges()
    {
        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        RestaurantEntity updatedRestaurant = restaurantService.updateRestaurant("","", restaurantId);

        assertThat(updatedRestaurant.getNom()).isEqualTo("Test RestaurantDTO");
        assertThat(updatedRestaurant.getAdresse()).isEqualTo("Ancienne adresse");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
        verify(restaurantRepository).save(restaurant);
    }


    // -------------------------------Test de la méthode getRestaurants----------------------------------
    @Test
    void testGetRestaurantsWithoutEval()
    {
        RestaurantEntity restaurant1 = new RestaurantEntity();
        restaurant1.setId(1L);
        restaurant1.setNom("Restaurant 1");
        restaurant1.setAdresse("Adresse 1");

        RestaurantEntity restaurant2 = new RestaurantEntity();
        restaurant2.setId(2L);
        restaurant2.setNom("Restaurant 2");
        restaurant2.setAdresse("Adresse 2");

        when(restaurantRepository.findAll()).thenReturn(java.util.List.of(restaurant1, restaurant2));
        when(evaluationRepository.findAllByRestaurant_Id(1L)).thenReturn(java.util.List.of());
        when(evaluationRepository.findAllByRestaurant_Id(2L)).thenReturn(java.util.List.of());

        var restaurants = restaurantService.getRestaurants();

        assertThat(restaurants).hasSize(2);
        assertThat(restaurants).extracting("nom").containsExactlyInAnyOrder("Restaurant 1", "Restaurant 2");
        assertThat(restaurants).extracting("adresse").containsExactlyInAnyOrder("Adresse 1", "Adresse 2");
        assertThat(restaurants).extracting("noteMoyenne").containsExactlyInAnyOrder(-1.0f, -1.0f);
        verify(restaurantRepository).findAll();
        verify(evaluationRepository).findAllByRestaurant_Id(1L);
        verify(evaluationRepository).findAllByRestaurant_Id(2L);
    }

    @Test
    void testGetRestaurantsWithEvals()
    {
        RestaurantEntity restaurant1 = new RestaurantEntity();
        restaurant1.setId(1L);
        restaurant1.setNom("Restaurant 1");
        restaurant1.setAdresse("Adresse 1");

        RestaurantEntity restaurant2 = new RestaurantEntity();
        restaurant2.setId(2L);
        restaurant2.setNom("Restaurant 2");
        restaurant2.setAdresse("Adresse 2");

        EvaluationEntity evaluation1 = new EvaluationEntity();
        evaluation1.setRestaurant(restaurant1);
        evaluation1.setNote(4);

        EvaluationEntity evaluation2 = new EvaluationEntity();
        evaluation2.setRestaurant(restaurant2);
        evaluation2.setNote(3);

        EvaluationEntity evaluation3 = new EvaluationEntity();
        evaluation3.setRestaurant(restaurant2);
        evaluation3.setNote(5);

        when(restaurantRepository.findAll()).thenReturn(java.util.List.of(restaurant1, restaurant2));
        when(evaluationRepository.findAllByRestaurant_Id(1L)).thenReturn(java.util.List.of(evaluation1));
        when(evaluationRepository.findAllByRestaurant_Id(2L)).thenReturn(java.util.List.of(evaluation2, evaluation3));

        var restaurants = restaurantService.getRestaurants();

        assertThat(restaurants).hasSize(2);
        assertThat(restaurants).extracting("nom").containsExactlyInAnyOrder("Restaurant 1", "Restaurant 2");
        assertThat(restaurants).extracting("noteMoyenne").containsExactlyInAnyOrder(4.0f, 4.0f);

        verify(restaurantRepository).findAll();
        verify(evaluationRepository).findAllByRestaurant_Id(1L);
        verify(evaluationRepository).findAllByRestaurant_Id(2L);
    }

    // -------------------------------Test de la méthode getRestaurant----------------------------------
    @Test
    void testGetRestaurant()
    {
        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        RestaurantEntity foundRestaurant = restaurantService.getRestaurant(restaurantId);

        assertThat(foundRestaurant).isEqualTo(restaurant);
        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
    }

    @Test
    void testGetRestaurantNotFound()
    {
        long restaurantId = 1L;

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> restaurantService.getRestaurant(restaurantId));
        assertThat(exception.getMessage()).isEqualTo("Restaurant avec l'id 1 non trouvé.");
        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
    }

    // -------------------------------Test de la méthode addImageUrl----------------------------------

    @Test
    void testAddImageUrl()
    {
        long restaurantId = 1L;
        String imageUrl = "http://example.com/image";

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        String returnedUrl = restaurantService.addImageUrl(restaurantId, imageUrl);

        assertThat(returnedUrl).isEqualTo(imageUrl + "_letts_restoImg.jpg");
        assertThat(restaurant.getImageUrl()).isEqualTo(imageUrl + "_letts_restoImg.jpg");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void testAddImageUrlRestaurantNotFound()
    {
        long restaurantId = 1L;
        String imageUrl = "http://example.com/image";

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> restaurantService.addImageUrl(restaurantId, imageUrl));
        assertThat(exception.getMessage()).isEqualTo("Restaurant avec l'id 1 non trouvé.");

        verify(restaurantRepository).findById(Math.toIntExact(restaurantId));
    }


    // -------------------------------Test de la méthode deleteRestaurant----------------------------------

    @Test
    void testDeleteRestaurant()
    {
        long restaurantId = 1L;

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        restaurant.setNom("Test RestaurantDTO");
        restaurant.setAdresse("Ancienne adresse");

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.of(restaurant));

        RestaurantEntity deletedRestaurant = restaurantService.deleteRestaurant(restaurantId);

        assertThat(deletedRestaurant).isEqualTo(restaurant);
        verify(restaurantRepository).delete(restaurant);

    }

    @Test
    void testDeleteRestaurantNotFound()
    {
        long restaurantId = 1L;

        when(restaurantRepository.findById(Math.toIntExact(restaurantId))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> restaurantService.deleteRestaurant(restaurantId));

        verify(restaurantRepository, never()).delete(any());
    }

}
