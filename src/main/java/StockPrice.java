import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.SandboxOpenApi;

public class StockPrice extends ServiceCommand {

    public StockPrice(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String mistake = new String();
        try {
            String userName = user.getUserName();
            BankApi bankApi = new BankApi();
            var api = bankApi.connect();
            String ticker = params[0];
            var cost = bankApi.getStocksCost((SandboxOpenApi) api, ticker);
            String costOutPut = cost.toString();
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, costOutPut);
        } catch (Exception e){
            String userName = user.getUserName();
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
        }
    }
}
