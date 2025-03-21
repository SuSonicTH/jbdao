package net.weichware.jbdao.spec.writer;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
        appendLines(members.stream().map(this::memberDefinition));
        append(new AllArgsConstructorWriter(specification));
        append(new ResultSetConstructorWriter(specification));
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
