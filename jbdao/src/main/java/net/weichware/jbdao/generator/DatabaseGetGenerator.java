package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.TemplateUtil;
import net.weichware.jbdao.writer.Generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseGetGenerator extends Generator {
    private String columns;

    public DatabaseGetGenerator(Specification specification) throws IOException {
        super(specification);

        if (specification.generateDatabase()) {
            addImports();
            specification.getPrimary().ifPresent(this::appendGet);
            appendGetList();
            appendGetListStatement();
            appendStream();
            appendResultSetSpliterator();
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
                "java.util.Spliterator",
                "java.util.Spliterators",
                "java.util.function.Consumer",
                "java.util.stream.Stream",
                "java.util.stream.StreamSupport"
        );
    }

    private void appendGet(Member primary) {
        addImport("java.util.Optional");
        emptyLine();
        appendLine("public static Optional<Customer> get(Connection connection, long id) throws SQLException {");
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"select %s from CUSTOMER where ID = ?\")) {", getColumns(), primary.getDatabaseName());
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

    private void appendResultSetSpliterator() throws IOException {
        Map<String, String> classMap = new HashMap<>();
        classMap.put("CLASS", specification.getName());
        addPrivateClass(TemplateUtil.getTemplate("ResultSetSpliterator.template", classMap));
    }
}
