import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class TestCommand2 extends ServiceCommand {

    public TestCommand2(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String brokerAccId = params[0];
        BigDecimal balance = new BigDecimal(params[1]);
        bankApi.setAccountBalance((SandboxOpenApi) api, brokerAccId, balance);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Баланс пополнен");
    }
}
