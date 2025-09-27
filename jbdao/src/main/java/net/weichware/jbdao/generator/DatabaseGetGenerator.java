package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

import java.util.stream.Collectors;

public class DatabaseGetGenerator extends Generator {
    private String columns;

    public DatabaseGetGenerator(Specification specification) {
        super(specification);

        if (specification.generateDatabase()) {
            addImports();
            specification.primary().ifPresent(primary -> {
                appendGet(primary);
                appendExists(primary);
            });
            appendGetList();
            appendGetListStatement();
            appendStream();
            addPrivateClass(new ResultSetSpliteratorGenerator(specification));
        }
    }

    private void addImports() {
        addImport(
                "java.sql.Connection",
                "javax.sql.DataSource",
                "java.sql.PreparedStatement",
                "java.sql.ResultSet",
                "java.sql.SQLException",
                "java.util.ArrayList",
                "java.util.List",
                "java.util.stream.Stream",
                "java.util.stream.StreamSupport"
        );
    }

    private void appendGet(Member primary) {
        addImport("java.util.Optional");
        emptyLine();
        appendLine("public static Optional<%s> get(Connection connection, long %s) throws SQLException {", specification.name(), primary.name());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"select %s from %s where %s = ?\")) {", getColumns(), specification.databaseName(), primary.databaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.name());
        appendLine("try (ResultSet resultSet = preparedStatement.executeQuery()) {");
        appendLine("if (resultSet.next()) {");
        appendLine("return Optional.of(new %s(resultSet));", specification.name());
        appendLine("}");
        appendLine("}");
        appendLine("}");
        appendLine("return Optional.empty();");
        appendLine("}");

        emptyLine();
        appendLine("public static Optional<%s> get(DataSource dataSource, long %s) throws SQLException {", specification.name(), primary.name());
        appendLine("try (Connection connection = dataSource.getConnection()) {");
        appendLine("return get(connection, %s);", primary.name());
        appendLine("}");
        appendLine("}");

    }

    private void appendExists(Member primary) {
        emptyLine();
        appendLine("public static boolean exists(Connection connection, long %s) throws SQLException {", primary.name());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"select %s from %s where %s = ?\")) {", primary.databaseName(), specification.databaseName(), primary.databaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.name());
        appendLine("try (ResultSet resultSet = preparedStatement.executeQuery()) {");
        appendLine("if (resultSet.next()) {");
        appendLine("return true;");
        appendLine("}");
        appendLine("}");
        appendLine("}");
        appendLine("return false;");
        appendLine("}");

        emptyLine();
        appendLine("public static boolean exists(DataSource dataSource, long %s) throws SQLException {", primary.name());
        appendLine("try (Connection connection = dataSource.getConnection()) {");
        appendLine("return exists(connection, %s);", primary.name());
        appendLine("}");
        appendLine("}");
    }

    private void appendGetList() {
        emptyLine();
        appendLine("public static List<%s> getList(Connection connection) throws SQLException {", specification.name());
        appendLine("return getList(connection, \"select %s from %s\");", getColumns(), specification.databaseName());
        appendLine("}");

        emptyLine();
        appendLine("public static List<%s> getList(DataSource dataSource) throws SQLException {", specification.name());
        appendLine("try (Connection connection = dataSource.getConnection()) {");
        appendLine("return getList(connection);", getColumns(), specification.databaseName());
        appendLine("}");
        appendLine("}");

    }

    private String getColumns() {
        if (columns == null) {
            columns = members.stream()
                    .map(Member::databaseName)
                    .collect(Collectors.joining(", "));
        }
        return columns;
    }

    private void appendGetListStatement() {
        String className = specification.name();
        emptyLine();
        appendLine("public static List<%s> getList(Connection connection, String sql, Object... args) throws SQLException {", className);
        appendLine("final List<%s> list = new ArrayList<>();", className);
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {");
        appendLine("int i = 1;");
        appendLine("for (Object arg : args) {");
        appendLine("preparedStatement.setObject(i++, arg);");
        appendLine("}");
        appendLine("try (ResultSet resultSet = preparedStatement.executeQuery()) {");
        appendLine("resultSet.setFetchSize(1000);");
        appendLine("while (resultSet.next()) {");
        appendLine("list.add(new %s(resultSet));", className);
        appendLine("}");
        appendLine("}");
        appendLine("}");
        appendLine("return list;");
        appendLine("}");

        emptyLine();
        appendLine("public static List<%s> getList(DataSource dataSource, String sql, Object... args) throws SQLException {", className);
        appendLine("try (Connection connection = dataSource.getConnection()) {");
        appendLine("return getList(connection, sql, args);");
        appendLine("}");
        appendLine("}");

    }

    private void appendStream() {
        emptyLine();
        appendLine("public static Stream<%s> stream(Connection connection) throws SQLException {", specification.name());
        appendLine("return stream(connection, \"select %s from %s \");", getColumns(), specification.databaseName());
        appendLine("}");

        emptyLine();
        appendLine("public static Stream<%s> stream(DataSource dataSource) throws SQLException {", specification.name());
        appendLine("return StreamSupport.stream(new ResultSetSpliterator(dataSource.getConnection(), \"select %s from %s \", true), false);", getColumns(), specification.databaseName());
        appendLine("}");


        emptyLine();
        appendLine("public static Stream<%s> stream(Connection connection, String sql, Object... args) throws SQLException {", specification.name());
        appendLine("return StreamSupport.stream(new ResultSetSpliterator(connection, sql, false, args), false);");
        appendLine("}");

        emptyLine();
        appendLine("public static Stream<%s> stream(DataSource dataSource, String sql, Object... args) throws SQLException {", specification.name());
        appendLine("return StreamSupport.stream(new ResultSetSpliterator(dataSource.getConnection(), sql, true, args), false);");
        appendLine("}");
    }

    private static class ResultSetSpliteratorGenerator extends Generator {
        protected ResultSetSpliteratorGenerator(Specification specification) {
            super(specification);
            addExtraClass("AbstractResultSetSpliterator.java");
            addImport("net.weichware.jbdao.AbstractResultSetSpliterator");
            addExtraClass("ResultSetSpliteratorException.java");

            emptyLine();
            appendLine("private static class ResultSetSpliterator extends AbstractResultSetSpliterator<%s> {", specification.name());
            emptyLine();
            appendLine("public ResultSetSpliterator(Connection connection, String sql, boolean closeConnection, Object... args) {");
            appendLine("super(connection, sql, closeConnection, args);");
            appendLine("}");
            emptyLine();
            appendLine("@Override");
            appendLine("protected %s create(ResultSet resultSet) throws SQLException {", specification.name());
            appendLine("return new %s(resultSet);", specification.name());
            appendLine("}");
            appendLine("}");
        }
    }
}
