package M2fst.tp_note.dto.evaluations;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ImageEvalURLDTO {
    private List<String> imageUrls;

    public static ImageEvalURLDTO fromLinks(List<String> imageUrls) {
        return ImageEvalURLDTO.builder()
                .imageUrls(imageUrls)
                .build();
    }
}
