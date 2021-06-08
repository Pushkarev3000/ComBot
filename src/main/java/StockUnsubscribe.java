import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class StockUnsubscribe extends ServiceCommand {

    static Logger logger = Logger.getLogger(StockSubscribe.class.toString());
    static CandleApiSubscriber Listener;
    static AbsSender chatSender;
    static Long chatId;

    public StockUnsubscribe(String identifier, String description) {
        super(identifier, description);
        if (Listener == null) {
            Listener = new CandleApiSubscriber(logger, Executors.newSingleThreadExecutor(),chatSender,chatId);
        }
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String stockFigi = params[0];

        if (Listener.getSender() == null) {
            Listener.setSender(absSender);
            Listener.setChatId(chat.getId());
        }
        //var listener = new CandleApiSubscriber(logger, Executors.newSingleThreadExecutor(), absSender, chat.getId());
        bankApi.stockUnsubscribe(stockFigi);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,
                "Отписались");
    }
}