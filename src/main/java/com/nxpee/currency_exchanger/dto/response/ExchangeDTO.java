package com.nxpee.currency_exchanger.dto.response;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;

public class ExchangeDTO {
    private final CurrenciesDTO baseCurrency;
    private final CurrenciesDTO targetCurrency;
    private final Double rate;
    private final Double amount;
    private final Double convertedAmount;

    public ExchangeDTO(CurrenciesDTO baseCurrency, CurrenciesDTO targetCurrency, Double rate, Double amount, Double convertedAmount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
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

    public Double getAmount() {
        return amount;
    }

    public Double getConvertedAmount() {
        return convertedAmount;
    }
}
