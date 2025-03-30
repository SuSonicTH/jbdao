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
            Optional<Member> optionalPrimary = specification.getPrimary();
            if (optionalPrimary.isPresent()) {
                primary = optionalPrimary.get();
                appendInsert();
                appendUpdate();
                appendDelete();
            }
        }
    }

    private void appendInsert() {
        emptyLine();
        appendLine("public Customer insert(Connection connection) throws SQLException {");
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"insert into %s (%s) values(%s)\")) {", specification.getDatabaseName(), memberList(), valueList());
        int i = 1;
        for (Member member : members) {
            appendLine("preparedStatement.setObject(%d, %s);", i++, member.getName());
        }
        appendLine("preparedStatement.execute();");
        appendLine("}");
        appendLine("return this;");
        appendLine("}");
    }

    private String memberList() {
        return members.stream()
                .map(Member::getDatabaseName)
                .collect(Collectors.joining(", "));
    }

    private String valueList() {
        return members.stream()
                .map(member -> "?")
                .collect(Collectors.joining(", "));
    }

    private void appendUpdate() {
        emptyLine();
        appendLine("public Customer update(Connection connection) throws SQLException {");
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"update %s set %s where %s = ?\")) {", specification.getDatabaseName(), memberSetExpression(), primary.getDatabaseName());
        int i = 1;
        for (Member member : members) {
            if (!member.isPrimary()) {
                appendLine("preparedStatement.setObject(%d, %s);", i++, member.getName());
            }
        }
        appendLine("preparedStatement.setObject(%d, %s);", i, primary.getName());
        appendLine("preparedStatement.execute();");
        appendLine("}");
        appendLine("return this;");
        appendLine("}");
    }

    private String memberSetExpression() {
        return members.stream()
                .filter(member -> !member.isPrimary())
                .map(member -> member.getDatabaseName() + " = ?")
                .collect(Collectors.joining(", "));
    }

    private void appendDelete() {
        emptyLine();
        appendLine("public void delete(Connection connection) throws SQLException {");
        appendLine("try (PreparedStatement preparedStatement = connection.prepareStatement(\"delete from %s where %s = ?\")) {", specification.getDatabaseName(), primary.getDatabaseName());
        appendLine("preparedStatement.setObject(1, %s);", primary.getName());
        appendLine("preparedStatement.execute();");
        appendLine("}");
        appendLine("}");
    }


}
