package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;

import java.sql.SQLException;
import java.util.Collection;

public abstract class Repository<T> {
    protected final DatabaseConnection connection;
    protected final static int BATCH_SIZE = 10;

    Repository(DatabaseConnection connection) {
        this.connection = connection;
    }

    public abstract T findById(int id) throws SQLException;

    public abstract void update(T model) throws SQLException;

    public abstract void updateMultiple(Collection<T> models) throws SQLException;

    public abstract void delete(T model) throws SQLException;

    public abstract void deleteMultiple(Collection<T> models) throws SQLException;

    public abstract void add(T model) throws SQLException;

    public abstract void addMultiple(Collection<T> models) throws SQLException;
}
