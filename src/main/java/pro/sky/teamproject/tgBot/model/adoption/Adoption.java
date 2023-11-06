package pro.sky.teamproject.tgBot.model.adoption;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import pro.sky.teamproject.tgBot.model.Animal;

import pro.sky.teamproject.tgBot.model.user.User;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "adoptions")
public class Adoption {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @FutureOrPresent
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate adoptedDate;

    @Future
    private LocalDate trialEndDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String note;

    public Adoption(Animal animal, User user) {
        this.animal = animal;
        this.user = user;
    }
}
