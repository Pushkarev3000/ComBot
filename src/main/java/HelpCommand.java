
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
public class HelpCommand extends ServiceCommand {

    public HelpCommand(String identifier, String description) {
        super(identifier, description);
    }

    private Logger logger = LoggerFactory.getLogger(HelpCommand.class);

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = user.getUserName();

        logger.debug(String.format("Пользователь %s. Начато выполнение команды %s", userName,
                this.getCommandIdentifier()));
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Я бот, который поможет получить актуальную информацию по акциям.\n\n" +
                        "❗*Список команд*\n" +
                        "/price + *тикер* - узнать цену акции\n" +
                        "/stock + *название акции* - узнать иформацию об акции\n" +
                        "/deposit + *сумма пополнения в $* - пополнить баланс\n" +
                        "/buy + *figi* + *штук* - купить акции\n" +
                        "/sell + *figi* + *штук* - продать акции\n" +
                        "/linearPrediction + *figi* + *ДД-ММ-ГГГГ* - линейно предсказать значение на дату\n" +
                        "/subscribe + *figi* - подписаться на рассылку информации по стоимости акции\n" +
                        "/unsubscribe + *figi* - отписаться от рассылки\n" +
                        "/showPortfolio - показать портфель\n" +
                        "/showBalance - показать баланс\n" +
                        "/help - помощь\n\n" +
                        "Желаю удачи\uD83D\uDE42"
        );
        logger.debug(String.format("Пользователь %s. Завершено выполнение команды %s", userName,
                this.getCommandIdentifier()));
    }
}