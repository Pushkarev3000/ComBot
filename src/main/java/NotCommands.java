
/**
     * Обработка сообщения, не являющегося командой (т.е. обычного текста не начинающегося с "/")
     */
   public class NotCommands {

    public String NotCommandExecute(Long chatId, String userName, String text) {
        //Settings settings;
        String answer;
        try {
            //создаём настройки из сообщения пользователя
            //settings = createSettings(text);
            //добавляем настройки в мапу, чтобы потом их использовать для этого пользователя при генерации файла
            //saveUserSettings(chatId, settings);
            answer = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            //логируем событие, используя userName
        /*} catch (IllegalArgumentException e) {
            answer = e.getMessage() +
                    "\n\n Настройки не были изменены. Вы всегда можете их посмотреть с помощью /settings";*/
            //логируем событие, используя userName
        } catch (Exception e) {
            answer = "Простите, я не понимаю Вас. Снова. Возможно, Вам поможет /help";
            //логируем событие, используя userName
        }
        return answer;
    }
}
