package pro.sky.teamproject.tgBot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.Objects;

@Data
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "shelters")
public class Shelter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название приюта
     */
    private String name;

    /**
     * Адрес приюта
     */
    @Column(name = "address_shelter")
    private String addressShelter;

    /**
     * Расписание работы
     */
    @Column(name = "working_time")
    private String workingTime;

    /**
     * Схема проезда
     */
    @Column(name = "driving_directions")
    private String drivingDirections;

    /**
     * Контактные данные охраны
     */
    @Column(name = "security_contact_details")
    private String securityContactDetails;

    private String info;

    @OneToMany(mappedBy = "shelter")
    private List<Animal> animal;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelter shelter = (Shelter) o;
        return Objects.equals(id, shelter.id) && Objects.equals(name, shelter.name) && Objects.equals(addressShelter, shelter.addressShelter) && Objects.equals(workingTime, shelter.workingTime) && Objects.equals(drivingDirections, shelter.drivingDirections) && Objects.equals(securityContactDetails, shelter.securityContactDetails) && Objects.equals(info, shelter.info) && Objects.equals(animal, shelter.animal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, addressShelter, workingTime, drivingDirections, securityContactDetails, info, animal);
    }

    @Override
    public String toString() {
        return "Shelter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addressShelter='" + addressShelter + '\'' +
                ", workingTime='" + workingTime + '\'' +
                ", drivingDirections='" + drivingDirections + '\'' +
                ", securityContactDetails='" + securityContactDetails + '\'' +
                ", info='" + info + '\'' +
                ", animal=" + animal +
                '}';
    }
}
