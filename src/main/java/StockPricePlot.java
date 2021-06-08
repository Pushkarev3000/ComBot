import graph.CandlesPlotDrawer;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.models.market.Candle;

import java.io.File;

public class StockPricePlot extends ServiceCommand {

    private static String TmpPhotoFilePath = "./plot_tmp.png";

    public StockPricePlot(String identifier, String description) {
        super(identifier, description);
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] params) {
        String userName = user.getUserName();
        var bankApi = new BankApi();
        bankApi.connect();

        String stockFigi = params[0];
        var candles = bankApi.getInstrumentCandles(stockFigi);
        var plotDrawer = new CandlesPlotDrawer(640, 480);
        var plot = plotDrawer.drawCandlesPlot(candles, stockFigi, "Prices");
        plotDrawer.saveToPng(plot, TmpPhotoFilePath);
        var inputFile = new InputFile(new File(TmpPhotoFilePath), "Plot");

        sendImage(absSender, chat.getId(), this.getCommandIdentifier(), userName, inputFile);
    }
}
