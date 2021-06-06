import lombok.SneakyThrows;
import org.reactivestreams.Subscriber;
import org.reactivestreams.example.unicast.AsyncSubscriber;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class StockSubscribePDV extends ServiceCommand {

    public StockSubscribePDV(String identifier, String description) {
        super(identifier, description);
    }



    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        class userS extends AsyncSubscriber<StreamingEvent.Candle> {
            protected userS(Executor executor) {
                super(executor);
            }

            @Override
            protected boolean whenNext(StreamingEvent.Candle element) {
                String messageText = String.format("UPDATE\n%s - %s", element.getFigi(), element.getHighestPrice());
                sendAnswer(absSender, chat.getId(), "subscribe", userName, messageText);
                return true;
            }
        }
        userS Listener = new userS(Executors.newSingleThreadExecutor(
        ));

        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String stockFigi = params[0];
        //bankApi.stockSubscribe(stockFigi, Listener);
        String messageText = String.format("Подписан на %s", stockFigi);
        sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, messageText);
    }
}