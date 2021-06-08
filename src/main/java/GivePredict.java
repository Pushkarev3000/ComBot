import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GivePredict extends ServiceCommand {

    public GivePredict(String identifier, String description) {
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
            String figi = params[0];
            OffsetDateTime dateTime4 = OffsetDateTime.of(LocalDateTime.of(2021, 07, 20, 12, 00),
                    ZoneOffset.ofHoursMinutes(6, 30));
            List<OffsetDateTime> dates = new ArrayList<OffsetDateTime>();
            dates.add(dateTime4);
            var prediction = Prediction.getsCandlesPrediction(bankApi.getInstrumentCandles(figi), dates);
            var predictionB = prediction[0];
            String predictOutPut = Double.toString(predictionB);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,predictOutPut);
        } catch (Exception e){
            String userName = user.getUserName();
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
            System.out.println(e.getStackTrace());
        }
    }
}
