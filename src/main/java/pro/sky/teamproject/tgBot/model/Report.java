package pro.sky.teamproject.tgBot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pro.sky.teamproject.tgBot.model.user.User;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

//    @Lob // указывает, что поле должно быть представлено как Large Object
    @Column(name = "photo_url")
    private String photo;

    private String info;

    @CreationTimestamp
    @Column(name = "datetime")
    private Timestamp datetime;

    @Column(name = "is_report_valid")
    private Boolean isReportValid;

}

