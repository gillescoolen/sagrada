package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Die;

import java.sql.SQLException;
import java.util.Collection;

public final class DieRepository extends Repository<Die> {

    public DieRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public Die findById(int id) throws SQLException {
        throw new SQLException("Die has no id");
    }

    @Override
    public void update(Die model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<Die> models) throws SQLException {

    }

    @Override
    public void delete(Die model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Die> models) throws SQLException {

    }

    @Override
    public void add(Die model) throws SQLException {
    }

    @Override
    public void addMultiple(Collection<Die> models) throws SQLException {

    }
}
