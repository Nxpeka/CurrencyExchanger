package com.nxpee.currency_exchanger.service;

import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.model.ExchangeRates;
import com.nxpee.currency_exchanger.repository.repositoryImpl.ExchangeRatesRepository;

import java.sql.SQLException;
import java.util.*;

public class ExchangeRatesService {
    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesRepository repository = ExchangeRatesRepository.getInstance();
    private ExchangeRatesService() {}

    public static ExchangeRatesService getInstance(){
        return INSTANCE;
    }

    public List<ExchangeRatesDTO> findAll() throws SQLException {
        ArrayList<ExchangeRatesDTO> exchangeRatesDTOS = new ArrayList<>();
        repository.findAll().forEach(value -> exchangeRatesDTOS.add(mapToExhangeRatesDTO(value)));
        return exchangeRatesDTOS;
    }

    public ExchangeRatesDTO findById(Integer id) throws SQLException {
        Optional<ExchangeRates> exchangeRates = repository.findById(id);
        return exchangeRates.map(this::mapToExhangeRatesDTO).orElse(null);
    }

    public ExchangeRatesDTO save(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        Integer genID = repository.save(mapToExhangeRates(exchangeRatesDTO));
        return new ExchangeRatesDTO(genID,
                exchangeRatesDTO.getBaseCurrencyId(),
                exchangeRatesDTO.getTargetCurrencyId(),
                exchangeRatesDTO.getRate());
    }

    public void delete(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        if(exchangeRatesDTO.getId() != null){
            repository.delete(mapToExhangeRates(exchangeRatesDTO));
        }
    }

    public void update(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        if(exchangeRatesDTO.getId() != null){
            repository.update(mapToExhangeRates(exchangeRatesDTO));
        }
    }

    private ExchangeRatesDTO mapToExhangeRatesDTO(ExchangeRates exchangeRates) {
        return new ExchangeRatesDTO(exchangeRates.getId(),
                exchangeRates.getBaseCurrencyId(),
                exchangeRates.getTargetCurrencyId(),
                exchangeRates.getRate());
    }

    private ExchangeRates mapToExhangeRates(ExchangeRatesDTO exchangeRatesDTO) {
        return new ExchangeRates(exchangeRatesDTO.getId(),
                exchangeRatesDTO.getBaseCurrencyId(),
                exchangeRatesDTO.getTargetCurrencyId(),
                exchangeRatesDTO.getRate());
    }
}
