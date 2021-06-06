import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.reactivestreams.Subscriber;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.Currency;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.orders.MarketOrder;
import ru.tinkoff.invest.openapi.models.orders.Operation;
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio;
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance;
import ru.tinkoff.invest.openapi.models.streaming.StreamingRequest;
import ru.tinkoff.invest.openapi.models.user.BrokerAccountType;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

public class BankApi {
    static Logger logger = Logger.getLogger(BankApi.class.toString());
    private static final Map<String, String> getenv = System.getenv();

    public OpenApi connect() throws ExecutionException, InterruptedException {
        OkHttpOpenApiFactory factory = new OkHttpOpenApiFactory(getenv.get("BANK_TOKEN"), logger);


        boolean sandboxMode = true;
        if (sandboxMode) {
            return factory.createSandboxOpenApiClient(Executors.newCachedThreadPool());
        } else {
            return factory.createOpenApiClient(Executors.newCachedThreadPool());
        }


        //CompletableFuture<Portfolio> brokerID;
        //System.out.println(brokerID);

        //портфель
        /*api.getUserContext().getAccounts().join({System.out.println(BrokerAccount);});
        api.getMarketContext().getMarketStocks().get().instruments.forEach(element ->{
            System.out.println(element.figi +" "+ element.name +" "+ element.currency);
        });*/

        //покупка
        /*api.getOrdersContext().placeMarketOrder("BBG000BGXZB5",new MarketOrder(1, Operation.Buy),
                api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
        api.getPortfolioContext().getPortfolio(api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get().positions.forEach(element ->{
            System.out.println(element.ticker +" "+ element.lots +" "+ element.balance);
        });*/

    }

    public String getBrokerAccountId(OpenApi api) {
        var acc = api.getUserContext().getAccounts().join();
        String brokerAccIdVar = acc.accounts.stream()
                .filter(brokerAccount -> brokerAccount.brokerAccountType == BrokerAccountType.Tinkoff)
                .findFirst().get().brokerAccountId;
        System.out.println(brokerAccIdVar);
        return brokerAccIdVar;
    }

    public void setAccountBalance(SandboxOpenApi api, String brokerAccId, BigDecimal balance) {
        api.getSandboxContext().setCurrencyBalance(new CurrencyBalance(Currency.USD, balance), brokerAccId);
    }

    public List<Portfolio.PortfolioPosition> showPortfolio (OpenApi api) throws ExecutionException, InterruptedException {
        var portfolioStats=  api.getPortfolioContext().getPortfolio(getBrokerAccountId(api)).
                get().positions.stream().collect(Collectors.toList());
        return portfolioStats;
    }

    public BigDecimal getAccountBalance(OpenApi api, String brokerAccId) {
        var portfolioCurrencies = api.getPortfolioContext().getPortfolioCurrencies(brokerAccId).join();
        var balance = portfolioCurrencies.currencies.stream()
                .filter(myCurrency -> myCurrency.currency == Currency.USD)
                .findFirst().get().balance;
        System.out.println(balance);
        return balance;
    }

    public BigDecimal getStocksCost(OpenApi api, String ticker) throws ExecutionException, InterruptedException {
        var name = api.getMarketContext().searchMarketInstrumentsByTicker(ticker).join();
        var cost = api.getMarketContext().getMarketCandles(
                name.instruments.get(0).figi,
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now().minusDays(1)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now()), ZoneOffset.UTC),
                CandleInterval.HOUR).join().get().candles.get(0).closePrice;
        System.out.println(cost);
        return cost;
    }

    public List<Instrument> streamStocks(OpenApi api, String stockName) throws ExecutionException, InterruptedException {
        var stream = api.getMarketContext().getMarketStocks().join().
                instruments.stream().filter(stockSearch -> stockSearch.name.toLowerCase(Locale.ROOT).
                contains(stockName.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        //stream().filter(stockSearch -> stockSearch.name.contains(stockName)).;
        return stream;
    }

    public void buyStock(OpenApi api, String figi, Integer lot) throws ExecutionException, InterruptedException {
        api.getOrdersContext().placeMarketOrder(figi, new MarketOrder(lot, Operation.Buy),
                api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
    }
    public void sellStock(OpenApi api, String figi, Integer lot) throws ExecutionException, InterruptedException {
        api.getOrdersContext().placeMarketOrder(figi, new MarketOrder(lot, Operation.Sell),
                api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
    }

    public void stockSubscribe(OpenApi api, String figi, Subscriber listener) {
        var streamingContext = api.getStreamingContext();
        streamingContext.getEventPublisher().subscribe(listener);
        streamingContext.sendRequest(StreamingRequest.subscribeCandle(figi, CandleInterval.ONE_MIN));
    }
}


