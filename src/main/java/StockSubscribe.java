import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class StockSubscribe extends ServiceCommand {

    static Logger logger = Logger.getLogger(StockSubscribe.class.toString());

    public StockSubscribe(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String stockFigi = params[0];

        var listener = new CandleApiSubscriber(logger, Executors.newSingleThreadExecutor(), absSender, chat.getId());
        bankApi.stockSubscribe(api, stockFigi, listener);
    }
}
