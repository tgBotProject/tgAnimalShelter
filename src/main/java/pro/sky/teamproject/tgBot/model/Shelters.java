package pro.sky.teamproject.tgBot.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity(name = "shelters")
public class Shelters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String info;
    @OneToMany(mappedBy = "shelter")
    private List<Animal> animal;

    public Shelters() {
    }

    public Shelters(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<Animal> getAnimal() {
        return animal;
    }

    public void setAnimal(List<Animal> animal) {
        this.animal = animal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shelters shelters = (Shelters) o;
        return Objects.equals(id, shelters.id) && Objects.equals(name, shelters.name) && Objects.equals(info, shelters.info) && Objects.equals(animal, shelters.animal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, info, animal);
    }

    @Override
    public String toString() {
        return "Shelters{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", info='" + info + '\'' +
                ", animal=" + animal +
                '}';
    }
}
