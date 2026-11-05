package M2fst.tp_note.controllers;

import M2fst.tp_note.dto.restaurants.*;
import M2fst.tp_note.repositories.S3Repository;
import M2fst.tp_note.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class RestaurantController
{
    private final RestaurantService restaurantService;
    private final S3Repository s3Repository;

    // Permet de créer un restaurant (accessible uniquement aux administrateurs)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/restaurants")
    public ResponseEntity<AddRestaurantResponseDTO> createRestaurant(@Valid @RequestBody CreateRestaurantDTO restaurant, @AuthenticationPrincipal Jwt principal)
    {
        return ResponseEntity.ok(AddRestaurantResponseDTO.fromEntity(this.restaurantService.createRestaurant(restaurant.getNom(), restaurant.getAdresse())));
    }

    // Permet de récupérer l'URL de l'image d'un restaurant
    @GetMapping("/restaurants/{id}/image")
    public ResponseEntity<String> getImageUrl(@PathVariable(name= "id") Long restaurantId)
    {
        String imageUrl = s3Repository.getRestoFileUrl(this.restaurantService.getRestaurant(restaurantId).getImageUrl());
        return ResponseEntity.ok(imageUrl);
    }

    // Permet de générer une URL d'upload pour l'image d'un restaurant (accessible uniquement aux administrateurs)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}/image")
    public ResponseEntity<String> uploadImageUrl(@PathVariable(name= "id") Long restaurantId, @AuthenticationPrincipal Jwt principal)
    {

        String imageUrl = s3Repository.getRestoUploadFileUrl(this.restaurantService.addImageUrl(restaurantId, String.valueOf(restaurantId)));
        return ResponseEntity.ok(imageUrl);
    }

    // Permet de mettre à jour un restaurant (accessible uniquement aux administrateurs)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<PutRestaurantResponseDTO> updateRestaurant(@PathVariable(name= "id") Long id, @Valid @RequestBody UpdateRestaurantDTO restaurantDTO, @AuthenticationPrincipal Jwt principal)
    {
        return ResponseEntity.ok(PutRestaurantResponseDTO.fromEntity(this.restaurantService.updateRestaurant(restaurantDTO.getNom(), restaurantDTO.getAdresse(), id)));
    }

    // Permet de récupérer la liste de tous les restaurants
    @GetMapping("/restaurants")
    public ResponseEntity<Collection<RestaurantDTO>>getRestaurants()
    {
        return ResponseEntity.ok(this.restaurantService.getRestaurants());
    }

    // Permet de récupérer un restaurant par son ID
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable(name= "id") Long id)
    {
        return ResponseEntity.ok(RestaurantDTO.fromEntity(this.restaurantService.getRestaurant(id), restaurantService.getNoteMoyenne(id)));
    }

    // Permet de supprimer un restaurant (accessible uniquement aux administrateurs)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<DeleteRestaurantResponseDTO> deleteRestaurant(@PathVariable(name= "id") Long id, @AuthenticationPrincipal Jwt principal)
    {
        s3Repository.deleteRestoImage(this.restaurantService.getRestaurant(id).getImageUrl());
        return ResponseEntity.ok(DeleteRestaurantResponseDTO.fromEntity(this.restaurantService.deleteRestaurant(id)));
    }


}
