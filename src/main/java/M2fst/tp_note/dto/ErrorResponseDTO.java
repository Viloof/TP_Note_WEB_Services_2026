package M2fst.tp_note.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDTO
{
    private final int code;
    private final String message;

    public ErrorResponseDTO(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

}