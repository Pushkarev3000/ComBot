/**
 * Обработка сообщения, не являющегося командой (т.е. обычного текста не начинающегося с "/")
 */
public class NotCommands {

    public String NotCommandExecute(Long chatId, String userName, String text) {
        String answer;
        try {
            answer = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
        } catch (Exception e) {
            answer = "Простите, что-то пошло не так. Возможно, Вам поможет /help";
        }
        return answer;
    }
}
