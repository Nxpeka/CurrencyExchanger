package com.nxpee.currency_exchanger.repository.repositoryImpl;

import com.nxpee.currency_exchanger.model.ExchangeRates;
import com.nxpee.currency_exchanger.repository.CrudRepository;
import com.nxpee.currency_exchanger.util.ConfiguredDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesRepository implements CrudRepository<ExchangeRates, Integer> {
    private final DataSource dataSource = ConfiguredDataSource.getINSTANCE();
    private static final ExchangeRatesRepository INSTANCE = new ExchangeRatesRepository();

    public static ExchangeRatesRepository getInstance() {
        return INSTANCE;
    }

    private ExchangeRatesRepository(){}

    private static final String SELECT_ALL = """
            SELECT * from exchange_rates
            """;
    private static final String SELECT_BY_ID = SELECT_ALL + """
            WHERE id = ?
            """;

    private static final String SAVE = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE exchange_rates SET base_currency_id = ?, target_currency_id = ?, rate = ? WHERE id = ?
            """;

    private static final String DELETE = """
            DELETE FROM exchange_rates WHERE id = ?
            """;

    @Override
    public Integer save(ExchangeRates entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)){

            statement.setInt(1, entity.getBaseCurrencyId());
            statement.setInt(2, entity.getTargetCurrencyId());
            statement.setDouble(3, entity.getRate());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if(generatedKeys.next()){
                return generatedKeys.getInt("id");
            }
        }
        return null;
    }

    @Override
    public Optional<ExchangeRates> findById(Integer id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return Optional.of(mapToExchangeRates(resultSet));
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<ExchangeRates> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL)){
            List<ExchangeRates> exchangeRates = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                exchangeRates.add(mapToExchangeRates(resultSet));
            }

            return exchangeRates;
        }

    }

    @Override
    public void update(ExchangeRates entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE)){

            statement.setInt(1, entity.getBaseCurrencyId());
            statement.setInt(2, entity.getTargetCurrencyId());
            statement.setDouble(3, entity.getRate());
            statement.setInt(4, entity.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(ExchangeRates entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE)){

            statement.setInt(1, entity.getId());

            statement.executeUpdate();
        }
    }

    private ExchangeRates mapToExchangeRates(ResultSet resultSet) throws SQLException {
        return new ExchangeRates(resultSet.getInt("id"),
                resultSet.getInt("base_currency_id"),
                resultSet.getInt("target_currency_id"),
                resultSet.getDouble("rate"));
    }
}
