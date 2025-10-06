package com.chainresource.exchangerate.strategies;

import com.chainresource.core.ChainResource;
import com.chainresource.core.Storage;
import com.chainresource.exchangerate.ExchangeRateChainResourceFactory;
import com.chainresource.exchangerate.ExchangeRateList;
import com.chainresource.storage.FileSystemStorage;
import com.chainresource.storage.MemoryStorage;
import com.chainresource.storage.WebServiceStorage;
import com.chainresource.utils.Constants;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class ExchangeRateChainResourceDefaultFactoryStrategy implements ExchangeRateChainResourceFactory {

    String CACHE_FILE = "exchange_rates_cache.json";

    ChainResource<ExchangeRateList> create() {
        String apiUrl = "https://openexchangerates.org/api/latest.json?app_id=" + Constants.API_KEY;

        List<Storage<ExchangeRateList>> chain = Arrays.asList(
                new MemoryStorage<>(Duration.ofHours(1)),
                new FileSystemStorage<>(CACHE_FILE, Duration.ofHours(4), ExchangeRateList.class),
                new WebServiceStorage<>(apiUrl, ExchangeRateList.class)
        );

        return new ChainResource<>(chain);
    }

}
