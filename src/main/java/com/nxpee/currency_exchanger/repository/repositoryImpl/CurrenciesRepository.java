package com.nxpee.currency_exchanger.repository.repositoryImpl;

import com.nxpee.currency_exchanger.model.Currencies;
import com.nxpee.currency_exchanger.repository.CrudRepository;
import com.nxpee.currency_exchanger.util.ConfiguredDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrenciesRepository implements CrudRepository<Currencies, Integer> {
    private final DataSource dataSource = ConfiguredDataSource.getINSTANCE();

    private final static CurrenciesRepository INSTANCE = new CurrenciesRepository();

    public static CurrenciesRepository getInstance() {
        return INSTANCE;
    }
    private CurrenciesRepository(){}

    private static final String SELECT_ALL = """
            SELECT * from currencies
            """;
    private static final String SELECT_BY_ID = SELECT_ALL + """
            WHERE id = ?
            """;

    private static final String SELECT_BY_CODE = SELECT_ALL + """
            WHERE code = ?
            """;

    private static final String SAVE = """
            INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?
            """;

    private static final String DELETE = """
            DELETE FROM currencies WHERE id = ?
            """;

    @Override
    public Integer save(Currencies entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();

            if(generatedKeys.next()){
                return generatedKeys.getInt("id");
            }
        }
        return null;
    }

    @Override
    public Optional<Currencies> findById(Integer id) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return Optional.of(mapToCurrencies(resultSet));
            }
        }
        return Optional.empty();
    }

    public Optional<Currencies> findByCode(String code) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_BY_CODE)){
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return Optional.of(mapToCurrencies(resultSet));
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Currencies> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL)){
            List<Currencies> currencies = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                currencies.add(mapToCurrencies(resultSet));
            }
            return currencies;
        }

    }

    @Override
    public void update(Currencies entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE)){

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());
            statement.setInt(4, entity.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(Currencies entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE)){

            statement.setInt(1, entity.getId());

            statement.executeUpdate();
        }
    }

    private Currencies mapToCurrencies(ResultSet resultSet) throws SQLException {
        return new Currencies(resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign"));
    }
}
