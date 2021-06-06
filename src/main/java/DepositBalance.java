import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

import java.math.BigDecimal;

public class DepositBalance extends ServiceCommand {

    public DepositBalance(String identifier, String description) {
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
            BigDecimal balance = new BigDecimal(params[0]);
            bankApi.setAccountBalance(brokerAccId, balance);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                    "Баланс пополнен");
        } catch (Exception e){
            String userName = user.getUserName();
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
        }
    }
}
