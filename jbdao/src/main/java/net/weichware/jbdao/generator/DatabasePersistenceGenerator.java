package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

import java.util.Optional;
import java.util.stream.Collectors;

public class DatabasePersistenceGenerator extends Generator {
    private Member primary;

    public DatabasePersistenceGenerator(Specification specification) {
        super(specification);
        if (specification.generateDatabase()) {
            Optional<Member> optionalPrimary = specification.primary();
            if (optionalPrimary.isPresent()) {
                primary = optionalPrimary.get();
                appendInsert();
                appendUpdate();
                appendDelete();
                appendIsInDatabase();
                appendPersist();
            } else {
                throw new RuntimeException("Database persistence without primary key not implemented");
            }
        }
    }

    private void appendInsert() {
        emptyLine();
        appendLine("public %s insert(Connection connection) throws SQLException {", specification.returnThisType());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"insert into %s (%s) values(%s)\")) {", specification.databaseName(), memberList(), valueList());
        int i = 1;
        for (Member member : members) {
            if (member.isEnum()) {
                appendLine("preparedStatement.setString(%d, %s.toDatabase());", i++, member.name());
            } else {
                appendLine("preparedStatement.setObject(%d, %s);", i++, member.name());
            }
        }
        appendLine("preparedStatement.execute();");
        appendLine("}");
        appendLine(specification.returnThis());
        appendLine("}");
    }

    private String memberList() {
        return members.stream()
                .map(Member::databaseName)
                .collect(Collectors.joining(", "));
    }

    private String valueList() {
        return members.stream()
                .map(member -> "?")
                .collect(Collectors.joining(", "));
    }

    private void appendUpdate() {
        emptyLine();
        appendLine("public %s update(Connection connection) throws SQLException {", specification.returnThisType());
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"update %s set %s where %s = ?\")) {", specification.databaseName(), memberSetExpression(), primary.databaseName());
        int i = 1;
        for (Member member : members) {
            if (!member.isPrimary()) {
                if (member.isEnum()) {
                    appendLine("preparedStatement.setString(%d, %s.toDatabase());", i++, member.name());
                } else {
                    appendLine("preparedStatement.setObject(%d, %s);", i++, member.name());
                }
            }
        }
        appendLine("preparedStatement.setObject(%d, %s);", i, primary.name());
        appendLine("if (preparedStatement.executeUpdate() != 1) {");
        appendLine("throw new SQLException(\"%s table not updated for primary key %s = '\" + %s + \"'\");", specification.databaseName(), specification.primary().get().databaseName(), specification.primary().get().name());
        appendLine("}");
        appendLine("}");
        appendLine(specification.returnThis());
        appendLine("}");
    }

    private String memberSetExpression() {
        return members.stream()
                .filter(member -> !member.isPrimary())
                .map(member -> member.databaseName() + " = ?")
                .collect(Collectors.joining(", "));
    }

    private void appendDelete() {
        emptyLine();
        appendLine("public void delete(Connection connection) throws SQLException {");
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"delete from %s where %s = ?\")) {", specification.databaseName(), primary.databaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.name());
        appendLine("preparedStatement.execute();");
        appendLine("}");
        appendLine("}");
    }

    private void appendIsInDatabase() {
        emptyLine();
        appendLine("public boolean isInDatabase(Connection connection) throws SQLException {");
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
    }

    private void appendPersist() {
        emptyLine();
        appendLine("public %s persist(Connection connection) throws SQLException {", specification.returnThisType());
        appendLine("if (isInDatabase(connection)) {");
        appendLine("return update(connection);");
        appendLine("}");
        appendLine("return insert(connection);");
        appendLine("}");
    }
}
