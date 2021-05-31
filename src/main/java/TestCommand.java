import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

public class TestCommand extends ServiceCommand {

    public TestCommand(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        String userName = user.getUserName();
            BankApi bankApi = new BankApi();
            var api = bankApi.connect();
            String brokerAccountId = bankApi.getBrokerAccountId(api);
            sendAnswer(absSender, chat.getId(),this.getCommandIdentifier(),userName,brokerAccountId);
    }
}
