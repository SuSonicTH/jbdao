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
            specification.getPrimary().ifPresent(primary -> {
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
        appendLine("public static Optional<Customer> get(Connection connection, long %s) throws SQLException {", primary.getName());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"select %s from %s where %s = ?\")) {", getColumns(), specification.getDatabaseName(), primary.getDatabaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.getName());
        appendLine("try (ResultSet resultSet = preparedStatement.executeQuery()) {");
        appendLine("if (resultSet.next()) {");
        appendLine("return Optional.of(new Customer(resultSet));");
        appendLine("}");
        appendLine("}");
        appendLine("}");
        appendLine("return Optional.empty();");
        appendLine("}");
    }

    private void appendExists(Member primary) {
        emptyLine();
        appendLine("public static boolean exists(Connection connection, long %s) throws SQLException {", primary.getName());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"select %s from %s where %s = ?\")) {", primary.getDatabaseName(), specification.getDatabaseName(), primary.getDatabaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.getName());
        appendLine("try (ResultSet resultSet = preparedStatement.executeQuery()) {");
        appendLine("if (resultSet.next()) {");
        appendLine("return true;");
        appendLine("}");
        appendLine("}");
        appendLine("}");
        appendLine("return false;");
        appendLine("}");
    }

    private void appendGetList() {
        emptyLine();
        appendLine("public static List<%s> getList(Connection connection) throws SQLException {", specification.getName());
        appendLine("return getList(connection, \"select %s from %s\");", getColumns(), specification.getDatabaseName());
        appendLine("}");
    }

    private String getColumns() {
        if (columns == null) {
            columns = members.stream()
                    .map(Member::getDatabaseName)
                    .collect(Collectors.joining(", "));
        }
        return columns;
    }

    private void appendGetListStatement() {
        String className = specification.getName();
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
    }

    private void appendStream() {
        emptyLine();
        appendLine("public static Stream<%s> stream(Connection connection) throws SQLException {", specification.getName());
        appendLine("return stream(connection, \"select %s from %s \");", getColumns(), specification.getDatabaseName());
        appendLine("}");
        emptyLine();
        appendLine("public static Stream<%s> stream(Connection connection, String sql, Object... args) throws SQLException {", specification.getName());
        appendLine("return StreamSupport.stream(new ResultSetSpliterator(connection, sql, args), false);");
        appendLine("}");
    }


    private static class ResultSetSpliteratorGenerator extends Generator {
        protected ResultSetSpliteratorGenerator(Specification specification) {
            super(specification);
            addExtraClass("AbstractResultSetSpliterator.java");
            addImport("net.weichware.jbdao.AbstractResultSetSpliterator");
            addExtraClass("ResultSetSpliteratorException.java");

            emptyLine();
            appendLine("private static class ResultSetSpliterator extends AbstractResultSetSpliterator<%s> {", specification.getName());
            emptyLine();
            appendLine("public ResultSetSpliterator(Connection connection, String sql, Object... args) {");
            appendLine("super(connection, sql, args);");
            appendLine("}");
            emptyLine();
            appendLine("@Override");
            appendLine("protected %s create(ResultSet resultSet) throws SQLException {", specification.getName());
            appendLine("return new %s(resultSet);", specification.getName());
            appendLine("}");
            appendLine("}");
        }
    }
}
