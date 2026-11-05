package M2fst.tp_note.controllers;

import M2fst.tp_note.dto.evaluations.*;
import M2fst.tp_note.repositories.S3Repository;
import M2fst.tp_note.services.EvaluationService;
import M2fst.tp_note.services.IndexService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EvaluationController
{
    private final EvaluationService evaluationService;
    private final IndexService indexService;
    private final S3Repository s3Repository;


    // Permet de créer une évaluation pour un restaurant donné
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/restaurants/{id}/evaluations")
    public ResponseEntity<CreateEvaluationResponseDTO> createEvaluation(@PathVariable(name= "id") Long id, @Valid @RequestBody CreateEvaluationDTO eval, @AuthenticationPrincipal Jwt principal)
    {
        indexService.indexEvaluation(id, evaluationService.getNextId(), eval.getCommentaire());
        return ResponseEntity.ok(CreateEvaluationResponseDTO.fromEntity
                (this.evaluationService.createEvaluation(eval.getCommentaire(), id, eval.getNote(),principal.getClaimAsString("preferred_username"))));
    }

    // Permet de mettre à jour une évaluation par son auteur ou par un administrateur
    @PreAuthorize("@evaluationSecurity.isOwnerOrAdmin(authentication, #id, principal.getClaimAsString('preferred_username'))")
    @PutMapping("/evaluations/{id}")
    public ResponseEntity<PutEvaluationResponseDTO> updateEvaluation(@PathVariable(name= "id") Long id, @Valid @RequestBody CreateEvaluationDTO eval, @AuthenticationPrincipal Jwt principal)
    {
        return ResponseEntity.ok(PutEvaluationResponseDTO.fromEntity(this.evaluationService.updateEvaluation(id, eval.getCommentaire(), eval.getNote())));
    }

    // Récupère les évaluations d'un restaurant créées par l'utilisateur connecté
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/restaurants/{id}/evaluations")
    public ResponseEntity<Collection<EvaluationDTO>> getEvaluationsByUser(@PathVariable(name= "id") Long id, @AuthenticationPrincipal Jwt principal)
    {
        return ResponseEntity.ok(this.evaluationService.getRestaurantEvaluationsByUser(id, principal.getClaimAsString("preferred_username")));
    }

    // Récupère les évaluations d'un restaurant pour tous les utilisateurs
    @GetMapping("/restaurants/{id}/evaluations/all")
    public ResponseEntity<Collection<EvaluationDTO>> getAllEvaluations(@PathVariable(name= "id") Long id)
    {
        return ResponseEntity.ok(this.evaluationService.getRestaurantEvaluations(id));
    }

    // Permet de récupérer les urls des images d'une évaluation et de générer des urls d'upload pour l'évaluation par son auteur ou par un administrateur
    @PreAuthorize("@evaluationSecurity.isOwnerOrAdmin(authentication, #id, principal.getClaimAsString('preferred_username'))")
    @PutMapping("/evaluations/{idEval}/images")
    public ResponseEntity<Collection<String>> uploadImageUrl(@PathVariable(name= "idEval") Long id, @RequestParam(name= "nbImgs") @Min(value = 0, message = "Le nombre de fichiers doit être au moins égal à 1.") @Max(value = 5, message = "Le nombre de fichiers doit être inférieur à 5.") int nbFiles, @AuthenticationPrincipal Jwt principal)
    {
        Collection<String> fileUrls = s3Repository.getEvalUploadFileUrls(this.evaluationService.addImageUrl(id, nbFiles));
        return ResponseEntity.ok(fileUrls);
    }


    // Permet de récuperer les urls des images d'une évaluation et d'obtenir les liens des images sur le bucket S3
    @GetMapping("/evaluations/{idEval}/images")
    public ResponseEntity<Collection<String>> getImageUrls(@PathVariable(name= "idEval") Long evaluationId)
    {
        Collection<String> imageUrls = s3Repository.getEvalFileUrls((List<String>) this.evaluationService.getImageUrls(evaluationId));
        return ResponseEntity.ok(imageUrls);
    }

    // Permet de rechercher des évaluations par restaurant et par mot-clé
    @GetMapping("/restaurants/{id}/evaluations/index")
    public ResponseEntity<Collection<String>> searchEvaluationsByResto(@PathVariable(name= "id") Long id, @RequestParam String query)
    {
        return ResponseEntity.ok(this.indexService.searchEvaluationsByResto(id, query));
    }

    // Permet de rechercher des évaluations par mot-clé
    @GetMapping("/evaluations/index")
    public ResponseEntity<Collection<String>> searchEvaluations(@RequestParam String query)
    {
        return ResponseEntity.ok(this.indexService.searchEvaluations(query));
    }

    // Permet de récupérer les évaluations d'un utilisateur
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluations")
    public ResponseEntity<Collection<EvaluationDTO>> getEvaluationsByUser(@AuthenticationPrincipal Jwt principal)
    {
        return ResponseEntity.ok(this.evaluationService.getEvaluationsByUser(principal.getClaimAsString("preferred_username")));
    }

    // Permet de récupérer toutes les évaluations
    @GetMapping("/evaluations/all")
    public ResponseEntity<Collection<EvaluationDTO>> getAllEvaluations()
    {
        return ResponseEntity.ok(this.evaluationService.getAllEvaluations());
    }

    // Permet de supprimer une évaluation par son auteur ou par un administrateur
    @PreAuthorize("@evaluationSecurity.isOwnerOrAdmin(authentication, #id, principal.getClaimAsString('preferred_username'))")
    @DeleteMapping("evaluations/{id}")
    public ResponseEntity<DeleteEvaluationResponseDTO> deleteEvaluation(@PathVariable(name= "id") Long id, @AuthenticationPrincipal Jwt principal)
    {
        s3Repository.deleteEvalImages((List<String>) this.evaluationService.getImageUrls(id));
        return ResponseEntity.ok(DeleteEvaluationResponseDTO.fromEntity(this.evaluationService.deleteEvaluation(id)));
    }


}
