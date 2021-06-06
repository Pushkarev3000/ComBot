import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class StockUnsubscribe extends ServiceCommand {

    static Logger logger = Logger.getLogger(StockUnsubscribe.class.toString());

    public StockUnsubscribe(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String stockFigi = params[0];

        bankApi.stockUnsubscribe(stockFigi);
        String messageText = String.format("Отписан от %s", stockFigi);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, messageText);
    }
}
