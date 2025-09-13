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
        appendLines(members.stream().map(member ->
                member.getName() + " = resultSet.getObject(" + quote(member.getDatabaseName()) + ", " + ClassUtil.primitiveToObjectMap.getOrDefault(member.getType(), member.getType()) + ".class);")
        );
        appendLine("validate();");
        appendLine("}");
    }

}
