import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class ShowPortfolio extends ServiceCommand {

    public ShowPortfolio(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String mistake;
        try {
            String userName = user.getUserName();
            BankApi bankApi = new BankApi();
            var api = bankApi.connect();
            String brokerAccId = bankApi.getBrokerAccountId();
            var portfolio = bankApi.showPortfolio();
            String portfolioOut = portfolio.toString();
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, portfolioOut);
        } catch (Exception e){
            String userName = user.getUserName();
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
        }
    }

}