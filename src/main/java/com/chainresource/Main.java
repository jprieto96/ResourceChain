import com.chainresource.core.ChainResource;
import com.chainresource.exchangerate.ExchangeRateList;
import com.chainresource.exchangerate.factory.ExchangeRateChainResourceFactory;
import com.chainresource.exchangerate.factory.strategies.ExchangeRateChainResourceDefaultFactoryStrategy;

void main() {

    ExchangeRateChainResourceFactory strategy = new ExchangeRateChainResourceDefaultFactoryStrategy();
    ChainResource<ExchangeRateList> exchangeRates = strategy.create();

    // First call - will fetch from API
    System.out.println("=== First call ===");
    exchangeRates.getValue().thenAccept(rates -> {
        System.out.println("Retrieved: " + rates);
        System.out.println("USD to EUR: " + rates.getRate("EUR"));
        System.out.println("USD to GBP: " + rates.getRate("GBP"));

        // Second call - will use memory cache
        System.out.println("\n=== Second call (should use memory cache) ===");
        exchangeRates.getValue().thenAccept(rates2 -> {
            System.out.println("Retrieved: " + rates2);
        }).join();
    }).join();

}
