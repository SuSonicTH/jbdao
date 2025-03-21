package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.CodeWriter;

import java.util.List;

public class ResultSetConstructor extends CodeWriter {
    private final Specification specification;
    private final List<Member> members;

    public ResultSetConstructor(Specification specification) {
        super(1);
        this.specification = specification;
        this.members = specification.getMembers();

        if (specification.isDatabase()) {
            generateCode();
        }
    }

    private void generateCode() {
        addImport("java.sql.ResultSet", "java.sql.SQLException");

        eol();
        appendLine("private %s(ResultSet resultSet) throws SQLException {", specification.getName());
        appendLines(members.stream().map(this::memberAssignment));
        appendLine("}");

    }

    private String memberAssignment(Member member) {
        return member.getName() + " = resultSet.getObject(" + quote(member.getDatabaseName()) + ", " + member.getType() + ".class);";
    }
}
