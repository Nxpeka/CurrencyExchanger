package com.nxpee.currency_exchanger.repository;

import java.sql.SQLException;
import java.util.Optional;

public interface CrudRepository<T, ID> {

    Integer save(T entity) throws SQLException;
    Optional<T> findById(ID id) throws SQLException;
    Iterable<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(T entity) throws SQLException;

}
