import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinearPredictionCommand extends ServiceCommand {

    private static ZoneOffset DefaultZoneOffset = ZoneOffset.ofHoursMinutes(6, 30);
    private static SimpleDateFormat ParamDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public LinearPredictionCommand(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String mistake = new String();
        String userName = user.getUserName();
        try {
            BankApi bankApi = new BankApi();
            bankApi.connect();

            String figi = params[0];
            String predictionDateStr = params[1];
            var predictionDate = ParamDateFormat.parse(predictionDateStr);
            var predictionOffsetDateTime = predictionDate.toInstant().atOffset(DefaultZoneOffset);

            List<OffsetDateTime> dates = Arrays.asList(predictionOffsetDateTime);
            var prediction = Prediction.getCandlesPrediction(bankApi.getInstrumentCandles(figi), dates)[0];
            String predictOutPut = Double.toString(prediction);
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName,predictOutPut);
        } catch (Exception e) {
            mistake = "Простите, я не понимаю Вас. Возможно, Вам поможет /help";
            sendAnswer(absSender, chat.getId(), this.getCommandIdentifier(), userName, mistake);
            System.out.println(e.getStackTrace());
        }
    }
}
