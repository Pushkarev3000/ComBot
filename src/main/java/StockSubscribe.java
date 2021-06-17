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
    static AbsSender chatSender;
    static Long chatId;
    static Long intervalMs = new Long(1000 * 60);

    public StockSubscribe(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        BankApi bankApi = new BankApi();
        bankApi.connect();
        String stockFigi = params[0];

        var listener = new CandleApiSubscriber(logger, Executors.newSingleThreadExecutor(), absSender, chat.getId(), intervalMs);
        bankApi.stockSubscribe(stockFigi, listener);
    }
}
