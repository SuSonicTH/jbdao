package net.weichware.jbdao.spec.writer;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
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
        appendAllArgsConstructors();
        appendResultSetConstructor();
        appendLine("}");
        writeSource(outputPath);
    }

    private void appendAllArgsConstructors() {
        if (specification.hasAllArgsConstructor()) {
            eol();
            appendLine("public %s(%s) {", specification.getName(), constructorArgumentList());
            if (specification.hasNonNullable()) {
                addImport("java.util.Objects");
                appendLines(objectsNotNull());
                eol();
            }
            if (specification.hasNonEmpty()) {
                appendLines(stringEmpty());
                eol();
            }
            appendLines(constructorAssignment());
            appendLine("}");
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

    private Stream<String> stringEmpty() {
        return members.stream()
                .filter(member -> member.getType().equals("String"))
                .filter(Member::getNotAcceptEmpty)
                .map(Member::getName)
                .map(name -> "if (" + name + ".isEmpty()) throw new IllegalArgumentException(" + quote(name + " may not be empty") + ");");

    }

    private Stream<String> objectsNotNull() {
        return members.stream()
                .filter(member -> !member.getNullable())
                .map(Member::getName)
                .map(name -> String.format("Objects.requireNonNull(%s,\"%s my not be null\");", name, name));
    }

    private Stream<String> constructorAssignment() {
        return members.stream()
                .map(Member::getName)
                .map(name -> String.format("this.%s = %s;", name, name));
    }

    private String constructorArgumentList() {
        return members.stream()
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

    private void appendMembers() {
        for (Member member : members) {
            appendLine("private%s%s %s;", member.getImmutable() ? " final " : " ", member.getType(), member.getName());
        }
    }
}
