import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class ShowBalance extends ServiceCommand {

    public ShowBalance(String identifier, String description) {
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
            BigDecimal balance = bankApi.getAccountBalance(brokerAccId);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, balance.toPlainString());
        } catch (Exception e){
            String userName = user.getUserName();
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
        }
    }

}
