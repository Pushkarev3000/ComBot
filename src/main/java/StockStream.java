import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

public class StockStream extends ServiceCommand {

    public StockStream(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String stockName = params[0];
        var stock = bankApi.streamStocks(stockName);
        String stockOut = stock.toString();
        //sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, stockOut);
        if (stockOut.equals("Optional.empty")) {
            stockOut = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, stockOut);
        } else {
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, stockOut);
        }

    }
}
