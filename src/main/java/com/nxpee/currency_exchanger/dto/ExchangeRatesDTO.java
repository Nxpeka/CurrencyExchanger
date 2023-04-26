package com.nxpee.currency_exchanger.dto;

public class ExchangeRatesDTO {
    private final Integer id;
    private final Integer baseCurrencyId;
    private final Integer targetCurrencyId;
    private final Double rate;
    public ExchangeRatesDTO(Integer id, Integer baseCurrencyId, Integer targetCurrencyId, Double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }
    public Integer getId() {
        return id;
    }
    public Integer getBaseCurrencyId() {
        return baseCurrencyId;
    }
    public Integer getTargetCurrencyId() {
        return targetCurrencyId;
    }
    public Double getRate() {
        return rate;
    }

}
