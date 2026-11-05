package M2fst.tp_note.services;

import M2fst.tp_note.repositories.EvaluationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("evaluationSecurity")
public class SecurityService
{

    @Autowired
    private EvaluationRepository evaluationRepository;

    public boolean isOwnerOrAdmin(Authentication authentication, Long evaluationId, String username)
    {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return true;

        return evaluationRepository.findById(Math.toIntExact(evaluationId))
                .map(eval -> eval.getAuteur().equals(username))
                .orElse(false);
    }
}