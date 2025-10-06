package com.chainresource.exchangerate;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateListTest {

    @Test
    void testExchangeRateListCreation() {
        ExchangeRateList list = new ExchangeRateList();
        assertNotNull(list.getRates());
        assertEquals(0, list.getRates().size());
    }

    @Test
    void testSettersAndGetters() {
        ExchangeRateList list = new ExchangeRateList();

        list.setBase("USD");
        list.setTimestamp(1234567890L);
        list.setDisclaimer("Test disclaimer");
        list.setLicense("Test license");

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.73);
        list.setRates(rates);

        assertEquals("USD", list.getBase());
        assertEquals(1234567890L, list.getTimestamp());
        assertEquals("Test disclaimer", list.getDisclaimer());
        assertEquals("Test license", list.getLicense());
        assertEquals(2, list.getRates().size());
    }

    @Test
    void testGetRate() {
        ExchangeRateList list = new ExchangeRateList();

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.73);
        list.setRates(rates);

        assertEquals(0.85, list.getRate("EUR"));
        assertEquals(0.73, list.getRate("GBP"));
        assertNull(list.getRate("JPY"));
    }

    @Test
    void testToString() {
        ExchangeRateList list = new ExchangeRateList();
        list.setBase("USD");
        list.setTimestamp(1234567890L);

        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 0.85);
        rates.put("GBP", 0.73);
        list.setRates(rates);

        String str = list.toString();
        assertTrue(str.contains("USD"));
        assertTrue(str.contains("1234567890"));
        assertTrue(str.contains("2"));
    }

}
