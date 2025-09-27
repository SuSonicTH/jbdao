package net.weichware.jbdaotest;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDatabase implements AutoCloseable {
    private final Connection connection;
    private static final String url  = "jdbc:h2:mem:JBDAODemo;DB_CLOSE_DELAY=0";;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public TestDatabase() throws SQLException {
        this.connection = DriverManager.getConnection(url, null, null);
    }

    public Connection getConnection() {
        return connection;
    }

    public void execute(String sql, Object... params) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            preparedStatement.executeUpdate();
        }
    }

    public DataSource getDataSource() {
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setUrl(url);
        return jdbcDataSource;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
