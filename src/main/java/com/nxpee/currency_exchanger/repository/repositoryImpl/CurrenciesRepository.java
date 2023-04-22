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

    private final String SELECT_ALL = """
                        SELECT * from currencies
                        """;
    private final String SELECT_BY_ID = SELECT_ALL + """
            WHERE id = ?
            """;

    private final String SAVE = """
            INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)
            """;

    private final String UPDATE = """
            UPDATE currencies SET code = ?, full_name = ?, sign = ? WHERE id = ?
            """;

    private final String DELETE = """
            DELETE FROM currencies WHERE id = ?
            """;

    @Override
    public Integer save(Currencies entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, entity.getCode());
            statement.setString(2, entity.getFullName());
            statement.setString(3, entity.getSign());

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
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
        catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Currencies entity) throws SQLException {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE)){

            statement.setInt(1, entity.getId());

            statement.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currencies mapToCurrencies(ResultSet resultSet) throws SQLException {
        return new Currencies(resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign"));
    }
}
