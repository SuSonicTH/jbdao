package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

public class ConstructorResultSetGenerator extends Generator {
    public ConstructorResultSetGenerator(Specification specification) {
        super(specification);

        if (specification.generateDatabase()) {
            generateCode();
        }
    }

    private void generateCode() {
        addImport("java.sql.ResultSet", "java.sql.SQLException");

        emptyLine();
        appendLine("protected %s(ResultSet resultSet) throws SQLException {", specification.className());
        appendLines(members.stream().map(member -> {
            if (member.isEnum()) {
                return String.format("%s = %s.fromDatabase(resultSet.getString(%s));", member.name(), member.type(), quote(member.databaseName()));
            } else {
                return String.format("%s = resultSet.getObject(%s, %s.class);", member.name(), quote(member.databaseName()), ClassUtil.primitiveToObjectMap.getOrDefault(member.type(), member.type()));
            }
        }));
        appendLine("validate();");
        appendLine("}");
    }

}
