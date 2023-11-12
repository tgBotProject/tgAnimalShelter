package pro.sky.teamproject.tgBot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import pro.sky.teamproject.tgBot.model.adoption.Adoption;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

//    @Lob // указывает, что поле должно быть представлено как Large Object
    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false)
    private Species species;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @JsonIgnore
    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY)
    private List<Adoption> adoptions;

    @ManyToOne
    @JoinColumn(name = "shelter_id") // имя колонки в таблице animals, которая будет хранить ID связанного приюта.
    private Shelter shelter;

    public Shelter getShelter() {
        return shelter;
    }

    public void setShelter(Shelter shelter) {
        this.shelter = shelter;
    }

    //  виды животных.
    public enum Species {
        CAT,
        DOG,
    }

    // пол животного.
    public enum Gender {
        MALE,
        FEMALE
    }

}

