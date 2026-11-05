package M2fst.tp_note.controllers;

import M2fst.tp_note.dto.ErrorResponseDTO;
import M2fst.tp_note.exceptions.BadRequestException;
import M2fst.tp_note.exceptions.InternalServerError;
import M2fst.tp_note.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // <-- Import crucial
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler
{

    // Capture l'erreur 404 levée lorsqu'une ressource n'est pas trouvée
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handle(NotFoundException ex)
    {
        return ResponseEntity.status(404).body(new ErrorResponseDTO(404, ex.getMessage()));
    }

    // Capture l'erreur 400 levée par notre code lorsqu'une requête est mal formée
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handle(BadRequestException ex)
    {
        return ResponseEntity.status(400).body(new ErrorResponseDTO(400, ex.getMessage()));
    }

    // Capture l'erreur 400 levée par Spring lorsqu'un paramètre de type incorrect est passé (ex: /restaurants/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handle(MethodArgumentTypeMismatchException ex)
    {
        return ResponseEntity.status(400).body(new ErrorResponseDTO(400, "Le paramètre doit être un nombre valide."));
    }

    // Capture l'erreur 403 levée par Spring Security (Fallait lire la doc)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handle(AccessDeniedException ex)
    {
        return ResponseEntity.status(403).body(new ErrorResponseDTO(403, "Accès interdit à cette ressource."));
    }

    // Capture l'erreur 500 levée lorsqu'une erreur interne survient
    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<ErrorResponseDTO> handle(InternalServerError ex)
    {
        return ResponseEntity.status(500).body(new ErrorResponseDTO(500, ex.getMessage()));
    }

    // Capture l'erreur 400 levée par Spring lorsqu'une validation échoue
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex)
    {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(400).body(new ErrorResponseDTO(400, message));
    }

    // Capture l'erreur 400 levée par Spring lorsqu'une validation échoue (utile pour les validations sur les paramètres de requête ici en particulier)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodValidation(HandlerMethodValidationException ex)
    {
        return ResponseEntity.status(400).body(new ErrorResponseDTO(400, "Le nombre de fichiers doit être compris entre 0 et 5."));
    }

}