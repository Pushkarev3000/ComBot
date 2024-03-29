import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public final class ComBot extends TelegramLongPollingCommandBot {
    private String BOT_NAME;
    private String BOT_TOKEN;

    //Класс для обработки сообщений, не являющихся командой
    private NotCommands NotCommands;

    public ComBot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;
        //создаём вспомогательный класс для работы с сообщениями, не являющимися командами
        this.NotCommands = new NotCommands();
        //регистрируем команды
        register(new StartCommand("start", "Старт"));
        register(new HelpCommand("help", "Помощь"));
        register(new DepositBalance("deposit","Пополнить баланс"));
        register(new ShowBalance("show_balance","Показать баланс аккаунта"));
        register(new StockPrice("price","Цена акции"));
        register(new ShowStock("stock","Информация по акции"));
        register(new BuyStock("buy","Покупка акций"));
        register(new SellStock("sell","Продажа акций"));
        register(new ShowPortfolio("show_portfolio","Показать портфель"));
        register(new StockPricePlot("plot", "Построить график цен"));
        register(new StockSubscribe("subscribe", "Подписаться на изменение цены акции"));
        register(new StockUnsubscribe("unsubscribe", "Отписаться от изменения цены акции"));
        register(new LinearPredictionCommand("linear_prediction", "Линейно предсказать цену акции"));
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Ответ на запрос, не являющийся командой
     */
    @Override
    public void processNonCommandUpdate(Update update) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        String answer = NotCommands.NotCommandExecute(chatId, userName, msg.getText());
        setAnswer(chatId, userName, answer);
    }

    /**
     * Формирование имени пользователя
     *
     * @param msg сообщение
     */
    private String getUserName(Message msg) {
        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    /**
     * Отправка ответа
     *
     * @param chatId   id чата
     * @param userName имя пользователя
     * @param text     текст ответа
     */
    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            //логируем сбой Telegram Bot API, используя userName
        }
    }
}
