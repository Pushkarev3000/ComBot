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


import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.Currency;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.orders.MarketOrder;
import ru.tinkoff.invest.openapi.models.orders.Operation;
import ru.tinkoff.invest.openapi.models.portfolio.Portfolio;
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance;
import ru.tinkoff.invest.openapi.models.user.BrokerAccountType;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

public class BankApi {
    static Logger logger = Logger.getLogger(BankApi.class.toString());
    private static final Map<String, String> getenv = System.getenv();

    private static OpenApi Api;
    private static boolean IsConnected;

    public boolean connect() throws ExecutionException, InterruptedException {
        if (IsConnected) {
            return IsConnected;
        }

        OkHttpOpenApiFactory factory = new OkHttpOpenApiFactory(getenv.get("BANK_TOKEN"), logger);
        boolean sandboxMode = true;
        IsConnected = true;
        if (sandboxMode) {
            Api = factory.createSandboxOpenApiClient(Executors.newCachedThreadPool());
        } else {
            Api = factory.createOpenApiClient(Executors.newCachedThreadPool());
        }

        return IsConnected;
    }

    public String getBrokerAccountId() {
        var acc = Api.getUserContext().getAccounts().join();
        String brokerAccIdVar = acc.accounts.stream()
                .filter(brokerAccount -> brokerAccount.brokerAccountType == BrokerAccountType.Tinkoff)
                .findFirst().get().brokerAccountId;
        System.out.println(brokerAccIdVar);
        return brokerAccIdVar;
    }

    public void setAccountBalance(String brokerAccId, BigDecimal balance) {
        ((SandboxOpenApi)Api).getSandboxContext().setCurrencyBalance(new CurrencyBalance(Currency.USD, balance), brokerAccId);
    }

    public List<Portfolio.PortfolioPosition> showPortfolio() throws ExecutionException, InterruptedException {
        var portfolioStats=  Api.getPortfolioContext().getPortfolio(getBrokerAccountId()).
                get().positions.stream().collect(Collectors.toList());
        return portfolioStats;
    }

    public BigDecimal getAccountBalance(String brokerAccId) {
        var portfolioCurrencies = Api.getPortfolioContext().getPortfolioCurrencies(brokerAccId).join();
        var balance = portfolioCurrencies.currencies.stream()
                .filter(myCurrency -> myCurrency.currency == Currency.USD)
                .findFirst().get().balance;
        System.out.println(balance);
        return balance;
    }

    public BigDecimal getStocksCost(String ticker) {
        var name = Api.getMarketContext().searchMarketInstrumentsByTicker(ticker).join();
        var cost = Api.getMarketContext().getMarketCandles(
                name.instruments.get(0).figi,
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now().minusDays(3)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now()), ZoneOffset.UTC),
                CandleInterval.HOUR).join().get().candles.get(0).closePrice;
        System.out.println(cost);
        return cost;
    }

    public List<Instrument> streamStocks(String stockName) {
        var stream = Api.getMarketContext().getMarketStocks().join().
                instruments.stream().filter(stockSearch -> stockSearch.name.toLowerCase(Locale.ROOT).
                contains(stockName.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
        return stream;
    }

    public void buyStock(String figi, Integer lot) throws ExecutionException, InterruptedException {
        Api.getOrdersContext().placeMarketOrder(figi, new MarketOrder(lot, Operation.Buy),
                Api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
    }

    public void sellStock(String figi, Integer lot) throws ExecutionException, InterruptedException {
        Api.getOrdersContext().placeMarketOrder(figi, new MarketOrder(lot, Operation.Sell),
                Api.getUserContext().getAccounts().get().accounts.get(0).brokerAccountId).get();
    }

}



