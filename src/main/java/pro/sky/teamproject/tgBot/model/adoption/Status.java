package pro.sky.teamproject.tgBot.model.adoption;

/**  Возможные статусы
 *     CURRENT - (дефолтное значение) текущий, испытательный срок;
 *     COMPLETED - успешно завершенный;
 *     CANCELED - отмененный (не пройденный).
 */
public enum Status {
    CURRENT("На испытательном сроке"),
    COMPLETED("Успешно завершено"),
    CANCELED("Галя, у нас отмена");

    private String russianDescription;

    Status(String russianDescription) {
        this.russianDescription = russianDescription;
    }

    public String getRussianDescription() {
        return russianDescription;
    }
}