package com.nxpee.currency_exchanger.dto;

public class ExchangeRatesDTO {
    private final Integer id;
    private final CurrenciesDTO baseCurrency;
    private final CurrenciesDTO targetCurrency;
    private final Double rate;
    public ExchangeRatesDTO(Integer id, CurrenciesDTO baseCurrencyId, CurrenciesDTO targetCurrencyId, Double rate) {
        this.id = id;
        this.baseCurrency = baseCurrencyId;
        this.targetCurrency = targetCurrencyId;
        this.rate = rate;
    }
    public Integer getId() {
        return id;
    }
    public CurrenciesDTO getBaseCurrency() {
        return baseCurrency;
    }
    public CurrenciesDTO getTargetCurrency() {
        return targetCurrency;
    }
    public Double getRate() {
        return rate;
    }

}
