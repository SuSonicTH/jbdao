package net.weichware.jbdao;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class ConcreteClassGenerator extends ClassWriter {
    private static final Logger log = LoggerFactory.getLogger(DaoGenerator.class);
    private final Specification specification;
    private final List<Member> members;
    private final Path outputPath;

    public ConcreteClassGenerator(Specification specification, Path outputPath) {
        super(specification.getPackagePath(), specification.getName());
        this.specification = specification;
        this.members = specification.getMembers();
        this.outputPath = outputPath;
    }

    public void generate() throws IOException {
        Path outputFile = getOutputFilePath(outputPath, specification.getPackagePath(), specification.className());
        if (Files.exists(outputFile)) {
            log.info("skipping generating concrete class {}", specification.getName());
            return;
        } else {
            log.info("generating concrete class {}", specification.getName());
        }
        appendLine("public class %s extends %s<%s> {", specification.getName(), specification.className(), specification.getName());
        if (specification.hasNoArgsConstructor()) {
            emptyLine();
            appendLine("public %s() {", specification.getName());
            appendLine("super();");
            appendLine("}");
        }
        if (specification.hasNonNullConstructor()) {
            emptyLine();
            appendLine("public %s(%s) {", specification.getName(), nonNullConstructorArgumentList());
            appendLine("super(%s);", nonNullConstructorNameList());
            appendLine("}");
        }
        if (specification.hasAllArgsConstructor()) {
            emptyLine();
            appendLine("public %s(%s) {", specification.getName(), allArgsConstructorArgumentList());
            appendLine("super(%s);", allArgsConstructorNameList());
            appendLine("}");

        }
        if (specification.generateDatabase()) {
            addImport("java.sql.ResultSet", "java.sql.SQLException");
            addImport("java.sql.ResultSet", "java.sql.SQLException");

            emptyLine();
            appendLine("protected %s(ResultSet resultSet) throws SQLException {", specification.getName());
            appendLine("super(resultSet);");
            appendLine("}");
        }
        appendLine("}");
        memberImports();
        writeSource(specification.getName(), outputPath);
    }


    private String nonNullConstructorArgumentList() {
        return members.stream()
                .filter(Member::isNotNullable)
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

    private String nonNullConstructorNameList() {
        return members.stream()
                .filter(Member::isNotNullable)
                .map(Member::getName)
                .collect(joining(", "));
    }

    private String allArgsConstructorArgumentList() {
        return members.stream()
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

    private String allArgsConstructorNameList() {
        return members.stream()
                .map(Member::getName)
                .collect(joining(", "));
    }

    private void memberImports() {
        for (Member member : members) {
            if (!ClassUtil.javaBuildIn.contains(member.getType())) {
                String clazz = ClassUtil.knownClasses.get(member.getType());
                if (clazz != null) {
                    addImport(clazz);
                } else {
                    throw new IllegalArgumentException("type '" + member.getType() + " for member variable '" + member.getName() + "' is unknown");
                }
            }
        }
    }
}
