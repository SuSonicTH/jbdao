package net.weichware.jbdao;

import net.weichware.jbdao.generator.AllArgsConstructor;
import net.weichware.jbdao.generator.ResultSetConstructor;
import net.weichware.jbdao.generator.WithGenerator;
import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.ClassWriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DaoGenerator extends ClassWriter {
    private final Specification specification;
    private final List<Member> members;
    private final Path outputPath;

    public DaoGenerator(Specification specification, Path outputPath) {
        super(specification.getPackagePath(), specification.getName());
        this.specification = specification;
        this.members = specification.getMembers();
        this.outputPath = outputPath;
    }

    public void generate() throws IOException {
        appendLine("public class %s {", specification.getName());
        appendLines(members.stream().map(this::memberDefinition));
        append(new AllArgsConstructor(specification));
        append(new ResultSetConstructor(specification));
        append(new WithGenerator(specification));
        appendLine("}");
        writeSource(outputPath);
    }

    private String memberDefinition(Member member) {
        return String.format("private%s%s %s;",
                member.getImmutable() ? " final " : " ",
                member.getType(),
                member.getName()
        );
    }

}
