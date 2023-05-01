package com.nxpee.currency_exchanger.service;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.dto.response.ExchangeDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.exception.NotFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();
    
    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    private ExchangeService() {}

    public static ExchangeService getInstance() {
        return INSTANCE;
    }

    public ExchangeDTO calculate(Set<Map.Entry<String, String[]>> entrySet) throws InvalidParametersException, SQLException, NotFoundException {
        Map<String, Object> params = mapParams(entrySet);
        return calculate((String) params.get("from"),(String) params.get("to"),(Double) params.get("amount"));
    }
    public ExchangeDTO calculate(String from, String to, Double amount) throws SQLException, InvalidParametersException, NotFoundException {
        CurrenciesDTO baseCurrency = currenciesService.findByCode(from);
        CurrenciesDTO targetCurrency = currenciesService.findByCode(to);
        int i = 0;
        while (true){
            switch (i){
                case 0 -> {
                    try {
                        ExchangeRatesDTO pair = exchangeRatesService.findPair(from, to);
                        return new ExchangeDTO(baseCurrency,
                                targetCurrency,
                                pair.getRate(),
                                amount,
                                convertA(pair, amount));
                    } catch (NotFoundException e){
                        i++;
                    }
                } case 1 -> {
                    try {
                        ExchangeRatesDTO pair = exchangeRatesService.findPair(to, from);
                        return new ExchangeDTO(baseCurrency,
                                targetCurrency,
                                pair.getRate(),
                                amount,
                                convertB(pair, amount));
                    } catch (NotFoundException e){
                        i++;
                    }
                } case 2 -> {
                    try {
                        ExchangeRatesDTO USDToBaseCurrency = exchangeRatesService.findPair("USD", from);
                        ExchangeRatesDTO USDtoTargetCurrency = exchangeRatesService.findPair("USD", to);
                        return new ExchangeDTO(baseCurrency,
                                targetCurrency,
                                calculateRate(USDToBaseCurrency, USDtoTargetCurrency),
                                amount,
                                convertC(USDToBaseCurrency, USDtoTargetCurrency, amount));

                    } catch (NotFoundException e){
                        throw new NotFoundException("Cant convert");
                    }
                }
            }
        }
    }

    private Map<String, Object> mapParams(Set<Map.Entry<String, String[]>> entrySet) throws InvalidParametersException {
        if(entrySet.size() != 3){throw new InvalidParametersException("Invalid parameters");}
        StringBuilder error = new StringBuilder();
        String from = null, to = null;
        Double amount = null;

        for (Map.Entry<String, String[]> entry : entrySet){
            switch (entry.getKey()) {
                case "from" -> {
                    if(!entry.getValue()[0].isBlank()){
                        if(entry.getValue()[0].length() == 3){
                            from = entry.getValue()[0];
                            break;
                        }
                    }
                    error.append(" from");
                }
                case "to" -> {
                    if(!entry.getValue()[0].isBlank()){
                        if(entry.getValue()[0].length() == 3) {
                            to = entry.getValue()[0];
                            break;
                        }
                    }
                    error.append(" to");
                }
                case "amount" -> {
                    if(!entry.getValue()[0].isBlank()) {
                        amount = parseInteger(entry.getValue()[0]);
                        if(amount != Double.MIN_VALUE){break;}
                        amount = null;
                    }
                    error.append(" amount");
                }
            }
        }

        if(from != null && to != null && amount != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("from", from.toUpperCase());
            params.put("to", to.toUpperCase());
            params.put("amount", amount);

            return params;
        }

        throw new InvalidParametersException("Invalid parameters:" + error);
    }

    private Double parseInteger(String str){
        try {
            return Double.parseDouble(str);
        }catch (NumberFormatException e){
            return Double.MIN_VALUE;
        }
    }

    private Double convertA(ExchangeRatesDTO exchangeRatesDTO, Double amount){
        return exchangeRatesDTO.getRate() * amount;
    }
    private Double convertB(ExchangeRatesDTO exchangeRatesDTO, Double amount){
        return amount / exchangeRatesDTO.getRate();
    }
    private Double convertC(ExchangeRatesDTO USDToBaseCurrency, ExchangeRatesDTO USDtoTargetCurrency, Double amount){
        return calculateRate(USDToBaseCurrency, USDtoTargetCurrency) * amount;
    }

    private Double calculateRate(ExchangeRatesDTO USDToBaseCurrency, ExchangeRatesDTO USDtoTargetCurrency) {
        return USDtoTargetCurrency.getRate() / USDToBaseCurrency.getRate();
    }
}
