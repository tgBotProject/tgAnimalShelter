package pro.sky.teamproject.tgBot.model.adoption;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pro.sky.teamproject.tgBot.model.Animal;
import pro.sky.teamproject.tgBot.model.User;

import java.time.LocalDate;

@Entity
@Data
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
    @NotNull
    private LocalDate adoptedDate;

    @Future
    @NotNull
    private LocalDate trialEndDate;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus adoptionStatus;
}
