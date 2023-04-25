package com.nxpee.currency_exchanger.service;

import com.nxpee.currency_exchanger.dto.CurrenciesDTO;
import com.nxpee.currency_exchanger.model.Currencies;
import com.nxpee.currency_exchanger.repository.repositoryImpl.CurrenciesRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public CurrenciesDTO findById(Integer id) throws SQLException {
        Optional<Currencies> currencies = repository.findById(id);
        return currencies.map(this::mapToCurrenciesDTO).orElse(null);
    }

    public CurrenciesDTO save(CurrenciesDTO currenciesDTO) throws SQLException {
        Integer genID = repository.save(mapToCurrencies(currenciesDTO));
        return new CurrenciesDTO(genID, currenciesDTO.getCode(), currenciesDTO.getFullName(), currenciesDTO.getSign());
    }

    private Currencies mapToCurrencies(CurrenciesDTO currenciesDTO) {
        return new Currencies(currenciesDTO.getId(),
                currenciesDTO.getCode(),
                currenciesDTO.getFullName(),
                currenciesDTO.getSign());
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


    private CurrenciesDTO mapToCurrenciesDTO(Currencies currencies) {
        return new CurrenciesDTO(currencies.getId(),
                currencies.getCode(),
                currencies.getFullName(),
                currencies.getSign());
    }

}
