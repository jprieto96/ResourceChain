package com.chainresource.exchangerate;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRateList {

    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String, Double> rates;

    public ExchangeRateList() {
        this.rates = new HashMap<>();
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public Double getRate(String currency) {
        return rates.get(currency);
    }

    @Override
    public String toString() {
        return String.format("ExchangeRate{base=%s, timestamp=%d, ratesCount=%d}",
                base, timestamp, rates != null ? rates.size() : 0);
    }

}
