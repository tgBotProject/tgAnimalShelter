package pro.sky.teamproject.tgBot.model;

import jakarta.persistence.*;
import lombok.*;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    /**
     * Техника безопасности
     */
    @Column(name = "safety_technique")
    private String safetyTechnique;
    /**
     * Правила знакомства
     */
    @Column(name = "dating_rules")
    private String datingRules;
    /**
     * Список документов
     */
    @Column(name = "doc_list")
    private String docList;
    /**
     * Рекомендации по транспортировке
     */
    @Column(name = "advice_transport")
    private String adviceTransport;
    /**
     * Рекомендации по обустройству для детёныша
     */
    @Column(name = "advice_cub")
    private String adviceCub;
    /**
     * Рекомендации по обустройству для взрослого животного
     */
    @Column(name = "advice_adult")
    private String adviceAdult;
    /**
     * Рекомендации по обустройству для ограниченного животного
     */
    @Column(name ="advice_invalid")
    private String adviceInvalid;
    /**
     * Причины отказа
     */
    @Column(name = "refusal_reasons")
    private String refusalReasons;
    /**
     * Дополнительная инфа(В случаи с собачим приютом это советы кинолога)
     */
    @Column(name = "dop_info")
    private String dopInfo;
    /**
     * Общая информация
     */
    private String info;

    @OneToMany(mappedBy = "shelter")
    private List<Animal> animal;
}