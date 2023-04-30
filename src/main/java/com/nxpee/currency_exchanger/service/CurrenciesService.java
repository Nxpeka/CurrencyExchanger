package com.nxpee.currency_exchanger.service;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.exception.InvalidParametersException;
import com.nxpee.currency_exchanger.model.Currencies;
import com.nxpee.currency_exchanger.repository.repositoryImpl.CurrenciesRepository;

import java.sql.SQLException;
import java.util.*;

public class CurrenciesService {
    private static final CurrenciesService INSTANCE = new CurrenciesService();
    private final CurrenciesRepository repository = CurrenciesRepository.getInstance();

    public static CurrenciesService getInstance() {
        return INSTANCE;
    }

    private CurrenciesService() {}

    public List<CurrenciesDTO> findAll() throws SQLException {
        ArrayList<CurrenciesDTO> currenciesDTOS = new ArrayList<>();
        repository.findAll().forEach(value -> currenciesDTOS.add(mapToCurrenciesDTO(value)));
        return currenciesDTOS;
    }

    public Optional<CurrenciesDTO> findById(Integer id) throws SQLException {
        Optional<Currencies> currencies = repository.findById(id);
        return currencies.map(this::mapToCurrenciesDTO);
    }

    public CurrenciesDTO save(CurrenciesDTO currenciesDTO) throws SQLException {
        Integer genID = repository.save(mapToCurrencies(currenciesDTO));
        return new CurrenciesDTO(genID, currenciesDTO.getCode(), currenciesDTO.getName(), currenciesDTO.getSign());
    }

    public CurrenciesDTO save(Set<Map.Entry<String, String[]>> entrySet) throws SQLException, InvalidParametersException {
        Map<String, Object> mapParams = mapParams(entrySet);

        String code = (String) mapParams.get("code");
        String name = (String) mapParams.get("name");
        String sign = (String) mapParams.get("sign");

        Integer genID = repository.save(new Currencies(null, code, name, sign));
        return new CurrenciesDTO(genID, code, name, sign);
    }

    public Optional<CurrenciesDTO> findByCode(String code) throws SQLException, InvalidParametersException {
        if (code.isBlank() || code.length() < 3){
            throw new InvalidParametersException("Invalid code");
        }
        Optional<Currencies> currencies = repository.findByCode(code);
        return currencies.map(this::mapToCurrenciesDTO);
    }

    public void delete(CurrenciesDTO currenciesDTO) throws SQLException {
        if(currenciesDTO.getId() != null){
            repository.delete(mapToCurrencies(currenciesDTO));
        }
    }

    public void update(CurrenciesDTO currenciesDTO) throws SQLException {
        if(currenciesDTO.getId() != null){
            repository.update(mapToCurrencies(currenciesDTO));
        }
    }
    private Map<String, Object> mapParams(Set<Map.Entry<String, String[]>> entrySet) throws InvalidParametersException {
        if(entrySet.size() != 3){throw new InvalidParametersException("Invalid parameters");}
        StringBuilder error = new StringBuilder();
        String name = null, code = null, sign = null;
        for (Map.Entry<String, String[]> entry : entrySet){
            switch (entry.getKey()) {
                case "name" -> {
                    if(!entry.getValue()[0].isBlank()) {
                        if(entry.getValue()[0].length() <= 30) {
                            name = entry.getValue()[0];
                            break;
                        }
                        error.append(" name");
                    }
                }
                case "code" -> {
                    if(!entry.getValue()[0].isBlank()){
                        if(entry.getValue()[0].length() == 3){
                            code = entry.getValue()[0];
                            break;
                        }
                        error.append(" code");
                    }
                }
                case "sign" -> {
                    if(!entry.getValue()[0].isBlank()) {
                        if(entry.getValue()[0].length() == 3) {
                            sign = entry.getValue()[0];
                            break;
                        }
                        error.append(" sign");
                    }
                }
            }
        }
        if(name != null && code != null && sign != null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("name", name);
            params.put("code", code);
            params.put("sign", sign);

            return params;
        }

        throw new InvalidParametersException("Invalid parameters:");
    }

    private Currencies mapToCurrencies(CurrenciesDTO currenciesDTO) {
        return new Currencies(currenciesDTO.getId(),
                currenciesDTO.getCode(),
                currenciesDTO.getName(),
                currenciesDTO.getSign());
    }


    private CurrenciesDTO mapToCurrenciesDTO(Currencies currencies) {
        return new CurrenciesDTO(currencies.getId(),
                currencies.getCode(),
                currencies.getFullName(),
                currencies.getSign());
    }

}
