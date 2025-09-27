package net.weichware.jbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public abstract class AbstractResultSetSpliterator<T> extends Spliterators.AbstractSpliterator<T> implements AutoCloseable {
    private final PreparedStatement preparedStatement;
    private final ResultSet resultSet;
    private final Connection connection;
    private final boolean closeConnection;

    public AbstractResultSetSpliterator(Connection connection, String sql, boolean closeConnection, Object... args) {
        super(Long.MAX_VALUE, Spliterator.ORDERED);
        this.connection = connection;
        this.closeConnection = closeConnection;
        try {
            preparedStatement = connection.prepareStatement(sql);
            int i = 1;
            for (Object arg : args) {
                preparedStatement.setObject(i++, arg);
            }

            resultSet = preparedStatement.executeQuery();
            resultSet.setFetchSize(1000);
        } catch (SQLException sqlException) {
            try {
                close();
            } catch (SQLException ex) {
                //intentionally left blank
            }
            throw new ResultSetSpliteratorException("Could not create ResultSetSpliterator", sqlException);
        }
    }

    protected abstract T create(ResultSet resultSet) throws SQLException;

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        try {
            if (resultSet.next()) {
                action.accept(create(resultSet));
                return true;
            } else {
                close();
                return false;
            }
        } catch (SQLException e) {
            throw new ResultSetSpliteratorException("Could not advance to next record", e);
        }
    }

    public void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (preparedStatement != null) {
            preparedStatement.close();
        }
        if (closeConnection && connection != null) {
            connection.close();
        }
    }
}
