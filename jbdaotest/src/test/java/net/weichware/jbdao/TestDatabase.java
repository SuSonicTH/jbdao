package net.weichware.jbdao;

import org.junit.jupiter.api.TestInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class TestDatabase implements AutoCloseable {
    private final Connection connection;

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public TestDatabase(TestInfo testInfo) throws SQLException {
        Objects.requireNonNull(testInfo, "testInfo may not be null");
        String dbName = testInfo.getTestClass().get().getSimpleName() + "_" + testInfo.getTestMethod().get().getName();
        this.connection = DriverManager.getConnection("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=0", null, null);
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

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
