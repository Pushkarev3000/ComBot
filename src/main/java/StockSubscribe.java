import lombok.SneakyThrows;
import org.reactivestreams.Subscriber;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.models.market.Candle;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

public class StockSubscribe extends ServiceCommand {

    static Logger logger = Logger.getLogger(StockSubscribe.class.toString());
    static CandleApiSubscriber Listener;
    static AbsSender chatSender;
    static Long chatId;

    public StockSubscribe(String identifier, String description) {
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
        bankApi.stockSubscribe(stockFigi, Listener);
    }
}
