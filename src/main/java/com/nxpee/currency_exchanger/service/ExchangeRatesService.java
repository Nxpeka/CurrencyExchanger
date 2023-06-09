package com.nxpee.currency_exchanger.service;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.dto.ExchangeRatesDTO;
import com.nxpee.currency_exchanger.exception.AlreadyExistException;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.exception.NotFoundException;
import com.nxpee.currency_exchanger.model.ExchangeRates;
import com.nxpee.currency_exchanger.repository.repositoryImpl.ExchangeRatesRepository;

import java.sql.SQLException;
import java.util.*;

public class ExchangeRatesService {
    private static final ExchangeRatesService INSTANCE = new ExchangeRatesService();
    private final ExchangeRatesRepository repository = ExchangeRatesRepository.getInstance();
    private final CurrenciesService currenciesService = CurrenciesService.getInstance();
    private ExchangeRatesService() {}

    public static ExchangeRatesService getInstance(){
        return INSTANCE;
    }

    public List<ExchangeRatesDTO> findAll() throws SQLException, NotFoundException {
        ArrayList<ExchangeRatesDTO> exchangeRatesDTOS = new ArrayList<>();
        Iterable<ExchangeRates> all = repository.findAll();
        for (ExchangeRates rates: all){
            exchangeRatesDTOS.add(mapToExchangeRatesDTO(rates));
        }
        return exchangeRatesDTOS;
    }

    public ExchangeRatesDTO findById(Integer id) throws SQLException, NotFoundException {
        Optional<ExchangeRates> exchangeRates = repository.findById(id);
        if(exchangeRates.isEmpty()){
            throw new NotFoundException("ExchangeRate not Found");
        }
        return mapToExchangeRatesDTO(exchangeRates.get());
    }

    public ExchangeRatesDTO findPairById(Integer baseCurrencyId, Integer targetCurrencyId) throws SQLException, NotFoundException {
        CurrenciesDTO baseCurrency = currenciesService.findById(baseCurrencyId);
        CurrenciesDTO targetCurrency = currenciesService.findById(targetCurrencyId);

        Optional<ExchangeRates> exchangeRates = repository.findPair(baseCurrency.getId(), targetCurrency.getId());

        if (exchangeRates.isEmpty()){
            throw new NotFoundException("Pair not found");
        }

        return mapToExchangeRatesDTO(exchangeRates.get());
    }

    public ExchangeRatesDTO findPair(String baseCurrencyCode, String targetCurrencyCode) throws InvalidParametersException, SQLException, NotFoundException {
        CurrenciesDTO baseCurrency = currenciesService.findByCode(baseCurrencyCode);
        CurrenciesDTO targetCurrency = currenciesService.findByCode(targetCurrencyCode);

        Optional<ExchangeRates> exchangeRates = repository.findPair(baseCurrency.getId(), targetCurrency.getId());

        if (exchangeRates.isEmpty()){
            throw new NotFoundException("Pair not found");
        }
        return mapToExchangeRatesDTO(exchangeRates.get());
    }

    public ExchangeRatesDTO findPair(String pair) throws InvalidParametersException, SQLException, NotFoundException {
        if(pair.isBlank() || pair.length() < 6){
            throw new InvalidParametersException("Invalid pair");
        }
        String baseCurrencyCode = pair.substring(0, 3);
        String targetCurrencyCode = pair.substring(3);

        return findPair(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRatesDTO save(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        Integer genID = repository.save(mapToExchangeRates(exchangeRatesDTO));
        return new ExchangeRatesDTO(genID,
                exchangeRatesDTO.getBaseCurrency(),
                exchangeRatesDTO.getTargetCurrency(),
                exchangeRatesDTO.getRate());
    }

    public ExchangeRatesDTO save(Set<Map.Entry<String, String[]>> entrySet) throws SQLException, InvalidParametersException, AlreadyExistException, NotFoundException {
        Map<String, Object> mapParams = mapParams(entrySet);

        CurrenciesDTO baseCurrenciesDTO = currenciesService.findByCode((String) mapParams.get("baseCurrencyCode"));
        CurrenciesDTO targetCurrenciesDTO = currenciesService.findByCode((String) mapParams.get("targetCurrencyCode"));
        Double doubleRate = (Double) mapParams.get("rate");

        if(existExchangeRate(baseCurrenciesDTO.getId(), targetCurrenciesDTO.getId())){
            throw new AlreadyExistException("ExchangeRate for pair: " + baseCurrenciesDTO.getCode() + ", " + targetCurrenciesDTO.getCode() + " already exist");
        }

        Integer genID = repository.save(new ExchangeRates(null, baseCurrenciesDTO.getId(), targetCurrenciesDTO.getId(), doubleRate));
        return new ExchangeRatesDTO(genID, baseCurrenciesDTO, targetCurrenciesDTO, doubleRate);
    }

    public void delete(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        if(exchangeRatesDTO.getId() != null){
            repository.delete(mapToExchangeRates(exchangeRatesDTO));
        }
    }

    public void update(ExchangeRatesDTO exchangeRatesDTO) throws SQLException {
        if(exchangeRatesDTO.getId() != null){
            repository.update(mapToExchangeRates(exchangeRatesDTO));
        }
    }

    public ExchangeRatesDTO updateRate(ExchangeRatesDTO updatingExchangeRatesDTO, String sRate) throws SQLException, InvalidParametersException {
        if(sRate == null || sRate.isEmpty() || sRate.isBlank() || !sRate.contains("rate")){
            throw new InvalidParametersException("Invalid rate");
        }

        Double rate = parseDouble(sRate.replace("rate=", ""));
        if(rate == Double.MIN_VALUE){throw new InvalidParametersException("Invalid rate");}

        ExchangeRatesDTO exchangeRatesDTO = new ExchangeRatesDTO(updatingExchangeRatesDTO.getId(),
                updatingExchangeRatesDTO.getBaseCurrency(),
                updatingExchangeRatesDTO.getTargetCurrency(),
                rate);

        update(exchangeRatesDTO);
        return exchangeRatesDTO;
    }

    private static Double parseDouble(String rate) {
        try {
            return Double.parseDouble(rate);
        }catch (NumberFormatException e){
            return Double.MIN_VALUE;
        }
    }

    private Map<String, Object> mapParams(Set<Map.Entry<String, String[]>> entrySet) throws InvalidParametersException {
        if(entrySet.size() != 3){throw new InvalidParametersException("Invalid parameters");}
        StringBuilder error = new StringBuilder();
        String baseCurrencyCode = null, targetCurrencyCode = null;
        Double rate = null;

        for (Map.Entry<String, String[]> entry : entrySet){
            switch (entry.getKey()) {
                case "baseCurrencyCode" -> {
                    if(!entry.getValue()[0].isBlank()){
                        if(entry.getValue()[0].length() == 3){
                            baseCurrencyCode = entry.getValue()[0];
                            break;
                        }
                    }
                    error.append(" baseCurrencyCode");
                }
                case "targetCurrencyCode" -> {
                    if(!entry.getValue()[0].isBlank()){
                        if(entry.getValue()[0].length() == 3) {
                            targetCurrencyCode = entry.getValue()[0];
                            break;
                        }
                    }
                    error.append(" targetCurrencyCode");
                }
                case "rate" -> {
                    if(!entry.getValue()[0].isBlank()) {
                        rate = parseDouble(entry.getValue()[0]);
                        if(rate != Double.MIN_VALUE){break;}
                        rate = null;
                    }
                    error.append(" rate");
                }
            }
        }
        if(baseCurrencyCode != null && targetCurrencyCode != null && rate != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("baseCurrencyCode", baseCurrencyCode);
            params.put("targetCurrencyCode", targetCurrencyCode);
            params.put("rate", rate);

            return params;
        }

        throw new InvalidParametersException("Invalid parameters:" + error);
    }

    private ExchangeRatesDTO mapToExchangeRatesDTO(ExchangeRates exchangeRates) throws SQLException, NotFoundException {
        CurrenciesDTO baseCurrencyDTO = currenciesService.findById(exchangeRates.getBaseCurrencyId());
        CurrenciesDTO targetCurrencyDTO = currenciesService.findById(exchangeRates.getTargetCurrencyId());

        return new ExchangeRatesDTO(exchangeRates.getId(),
                baseCurrencyDTO,
                targetCurrencyDTO,
                exchangeRates.getRate());
    }

    private ExchangeRates mapToExchangeRates(ExchangeRatesDTO exchangeRatesDTO) {
        return new ExchangeRates(exchangeRatesDTO.getId(),
                exchangeRatesDTO.getBaseCurrency().getId(),
                exchangeRatesDTO.getTargetCurrency().getId(),
                exchangeRatesDTO.getRate());
    }

    private boolean existExchangeRate(Integer baseCurrId, Integer targetCurrId) throws SQLException {
        try {
            findPairById(baseCurrId, targetCurrId);
            return true;
        } catch (NotFoundException e) {
            return true;
        }
    }
}
