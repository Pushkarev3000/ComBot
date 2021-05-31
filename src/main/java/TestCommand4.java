import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.market.HistoricalCandles;
import ru.tinkoff.invest.openapi.models.market.Instrument;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

public class TestCommand4 extends ServiceCommand {

    public TestCommand4(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        BankApi bankApi = new BankApi();
        var api = bankApi.connect();
        String ticker = params[0];
        var cost = bankApi.getStocksCost((SandboxOpenApi) api, ticker);
        String costOutPut = cost.toString();
        sendAnswer(absSender, chat.getId(),this.getCommandIdentifier(),userName, costOutPut);
    }
}
