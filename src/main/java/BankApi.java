import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.Currency;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance;
import ru.tinkoff.invest.openapi.models.user.BrokerAccountType;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

public class BankApi {
    static Logger logger = Logger.getLogger(BankApi.class.toString());
    private static final Map<String, String> getenv = System.getenv();

    public OpenApi connect() throws ExecutionException, InterruptedException {
        //String token = "t.z-ouhGdbTJ4KfeFAbCULPSa-0Dbpkb2x7SpzXUzwPlv1BDkg2smMPRZRXqy1Z7ZcPzNQOtzGvGg7OcwIeHhMiw";
        OkHttpOpenApiFactory factory = new OkHttpOpenApiFactory(getenv.get("BANK_TOKEN"), logger);


        boolean sandboxMode = true;
        if (sandboxMode) {
            return factory.createSandboxOpenApiClient(Executors.newCachedThreadPool());
        } else {
            return factory.createOpenApiClient(Executors.newCachedThreadPool());
        }

        //((SandboxOpenApi) api).getSandboxContext().performRegistration(new SandboxRegisterRequest()).join();
        //((SandboxOpenApi) api).getSandboxContext().performRegistration(BrokerAccountType.Tinkoff).join();


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

        //свечи
    }

    /*public void getAccuranceStock(OpenApi api) {
        var as = api.getMarketContext().getMarketCandles("BBG000BGXZB5",
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now().minusDays(5)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now()), ZoneOffset.UTC),
                CandleInterval.DAY).join().get().candles.forEach(element -> {
            System.out.println(element.closePrice);
        });
        //return as;
    }*/

    public String getBrokerAccountId(OpenApi api) {
        //api.getSandboxContext().performRegistration(null).join();
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

    public BigDecimal getAccountBalance(OpenApi api, String brokerAccId) {
        var portfolioCurrencies = api.getPortfolioContext().getPortfolioCurrencies(brokerAccId).join();
        var balance = portfolioCurrencies.currencies.stream()
                .filter(myCurrency -> myCurrency.currency == Currency.USD)
                .findFirst().get().balance;
        System.out.println(balance);
        return balance;

    }

    public BigDecimal getStocksCost (OpenApi api, String ticker) throws ExecutionException, InterruptedException {
        var name = api.getMarketContext().searchMarketInstrumentsByTicker(ticker).join();
        var cost = api.getMarketContext().getMarketCandles(
                name.instruments.get(0).figi,
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now().minusDays(5)), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.from(LocalDateTime.now()), ZoneOffset.UTC),
                CandleInterval.DAY).join().get().candles.get(0).highestPrice;
                //forEach(element -> { System.out.println(element.figi + " " + element.name + " " + element.currency); });
        System.out.println(cost);
        return cost;
    }
}
