package M2fst.tp_note.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class EvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contenu", length = 255)
    private String contenu;

    @Column(name = "auteur")
    private String auteur;

    @Column(name = "note")
    private int note;

    @ManyToOne
    private RestaurantEntity restaurant;

    @ElementCollection
    @CollectionTable(
            name = "images_eval",
            joinColumns = @JoinColumn(name = "eval_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_images_eval_eval_id_images",
                    columnNames = {"eval_id", "images"}
            )
    )
    @Column(name = "images", nullable = false)
    private List<String> images;

}
