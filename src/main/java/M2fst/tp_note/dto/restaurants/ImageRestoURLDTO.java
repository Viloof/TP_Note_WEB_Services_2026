package M2fst.tp_note.dto.restaurants;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageRestoURLDTO {
    private String imageUrl;

    public static ImageRestoURLDTO fromLink(String imageUrl) {
        return ImageRestoURLDTO.builder()
                .imageUrl(imageUrl)
                .build();
    }
}
