package pro.sky.teamproject.tgBot.model.adoption;

/**  Возможные статусы
 *     CURRENT - (дефолтное значение) текущий, испытательный срок;
 *     COMPLETED - успешно завершенный;
 *     CANCELED - отмененный (не пройденный).
 */
public enum Status {
    CURRENT,
    COMPLETED,
    CANCELED
}