package com.chainresource.exchangerate;

import com.chainresource.core.ChainResource;

public interface ExchangeRateChainResourceFactory {

    ChainResource<ExchangeRateList> create();

}
