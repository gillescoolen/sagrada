package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;

import java.sql.SQLException;

public abstract class Repository<T> {
    protected final DatabaseConnection connection;

    Repository(DatabaseConnection connection) {
        this.connection = connection;
    }

    public abstract T findById(int id) throws SQLException;

    public abstract void update(T model) throws SQLException;

    public abstract void updateMultiple(Iterable<T> models) throws SQLException;

    public abstract void delete(T model) throws SQLException;

    public abstract void deleteMultiple(Iterable<T> models) throws SQLException;

    public abstract void add(T model) throws SQLException;

    public abstract void addMultiple(Iterable<T> models) throws SQLException;
}
