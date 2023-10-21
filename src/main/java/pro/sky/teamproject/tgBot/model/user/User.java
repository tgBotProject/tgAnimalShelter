package pro.sky.teamproject.tgBot.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pro.sky.teamproject.tgBot.model.adoption.Adoption;

import java.sql.Timestamp;
import java.util.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;

    private String name;

    private String phone;

    @CreationTimestamp
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Adoption> adoptions;
}