package net.weichware.jbdao.spec.writer;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DaoWriter extends ClassWriter {
    private final Specification specification;
    private final List<Member> members;
    private final Path outputPath;

    public DaoWriter(Specification specification, Path outputPath) {
        super(specification.getPackagePath(), specification.getName());
        this.specification = specification;
        this.members = specification.getMembers();
        this.outputPath = outputPath;
    }

    public void generate() throws IOException {
        appendLine("public class %s {", specification.getName());
        appendMembers();
        append(new AllArgsConstructorWriter(specification));
        appendResultSetConstructor();
        appendLine("}");
        writeSource(outputPath);
    }

    private void appendMembers() {
        for (Member member : members) {
            appendLine("private%s%s %s;", member.getImmutable() ? " final " : " ", member.getType(), member.getName());
        }
    }

    private void appendResultSetConstructor() {
        if (specification.isDatabase()) {
            addImport("java.sql.ResultSet", "java.sql.SQLException");
            eol();
            appendLine("private %s(ResultSet resultSet) throws SQLException {", specification.getName());
            appendLines(resultSetAssignment());
            appendLine("}");
        }
    }

    private List<String> resultSetAssignment() {
        return members.stream()
                .map(member -> member.getName() + " = resultSet.getObject(" + quote(member.getDatabaseName()) + ", " + member.getType() + ".class);")
                .collect(toList());
    }


}
