package net.weichware.jbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            preparedStatement = prepareStatement(sql, args);
            preparedStatement.setFetchSize(500);
            setArguments(args);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException sqlException) {
            try {
                close();
            } catch (SQLException ex) {
                //intentionally left blank
            }
            throw new ResultSetSpliteratorException("Could not create ResultSetSpliterator", sqlException);
        }
    }

    private PreparedStatement prepareStatement(String sql, Object[] args) throws SQLException {
        String[] split = (sql + " ").split("\\?");
        if (args.length != split.length - 1) {
            throw new ValidationException("Got " + args.length + " arguments for " + (split.length - 1) + " question marks");
        }

        if (Arrays.stream(args).anyMatch(arg -> arg instanceof Collection)) {
            return prepareStatementWithCollections(split, args);
        } else {
            return connection.prepareStatement(sql);
        }
    }

    private PreparedStatement prepareStatementWithCollections(String[] sql, Object[] args) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append(sql[0]);
        int i = 0;
        for (Object arg : args) {
            if (arg instanceof Collection) {
                Collection col = (Collection) arg;
                query.append(IntStream.range(0, col.size())
                        .mapToObj(q -> "?")
                        .collect(Collectors.joining(","))
                );
            } else {
                query.append("?");
            }
            if (i < sql.length - 1) {
                query.append(sql[++i]);
            }
        }
        return connection.prepareStatement(query.toString());
    }

    private void setArguments(Object[] args) throws SQLException {
        int i = 1;
        for (Object arg : args) {
            if (arg instanceof Collection) {
                Collection col = (Collection) arg;
                for (Object item : col) {
                    preparedStatement.setObject(i++, item);
                }
            } else {
                preparedStatement.setObject(i++, arg);
            }
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
