package M2fst.tp_note.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class InternalServerError extends RuntimeException
{
    public InternalServerError(String message) {
        super(message);
    }
}
