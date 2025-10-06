package com.chainresource.exchangerate.factory;

import com.chainresource.core.ChainResource;
import com.chainresource.exchangerate.ExchangeRateList;

public interface ExchangeRateChainResourceFactory {

    ChainResource<ExchangeRateList> create();

}
