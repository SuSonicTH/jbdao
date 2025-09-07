package net.weichware.jbdao;

import net.weichware.jbdao.generator.BuilderGenerator;
import net.weichware.jbdao.generator.ConstructorAllArgsGenerator;
import net.weichware.jbdao.generator.ConstructorNoArgsGenerator;
import net.weichware.jbdao.generator.ConstructorNonNullGenerator;
import net.weichware.jbdao.generator.ConstructorResultSetGenerator;
import net.weichware.jbdao.generator.CsvGenerator;
import net.weichware.jbdao.generator.DatabaseGetGenerator;
import net.weichware.jbdao.generator.DatabasePersistenceGenerator;
import net.weichware.jbdao.generator.GetterSetterGenerator;
import net.weichware.jbdao.generator.HashEqualsGenerator;
import net.weichware.jbdao.generator.JsonGenerator;
import net.weichware.jbdao.generator.ToStringGenerator;
import net.weichware.jbdao.generator.ValidationGenerator;
import net.weichware.jbdao.generator.WithGenerator;
import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DaoGenerator extends ClassWriter {
    private static final Logger log = LoggerFactory.getLogger(DaoGenerator.class);
    private final Specification specification;
    private final List<Member> members;
    private final Path outputPath;

    public DaoGenerator(Specification specification, Path outputPath) {
        super(specification.getPackagePath(), specification.getName());
        this.specification = specification;
        this.members = specification.getMembers();
        this.outputPath = outputPath;
    }

    public static void main(String[] args) throws IOException {
        log.info("Starting codegen ");
        if (args.length != 2) {
            System.err.println("jbdao error: expecting exactly 2 arguments spec file/path and output path");
            System.exit(1);
        }

        Path specPath = Paths.get(args[0]);
        if (!Files.exists(specPath)) {
            System.err.println("error: spec file/path " + specPath + " does not exist");
            System.exit(2);
        }

        Path outputPath = Paths.get(args[1]);
        if (!Files.exists(specPath)) {
            Files.createDirectories(outputPath);
        } else if (!Files.isDirectory(outputPath)) {
            System.err.println("error: output path " + outputPath + " is not a directory");
            System.exit(3);
        }

        if (Files.isDirectory(specPath)) {
            for (Path file : getFileList(specPath)) {
                generateClass(file, outputPath);
            }
        } else {
            generateClass(specPath, outputPath);
        }
        log.info("Finished codegen");
    }

    private static List<Path> getFileList(Path specPath) throws IOException {
        try (Stream<Path> fileStream = Files.list(specPath)) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".json"))
                    .collect(Collectors.toList());
        }
    }

    private static void generateClass(Path specFile, Path outputPath) throws IOException {
        log.info("generating class for spec " + specFile.getFileName());
        String spec = new String(Files.readAllBytes(specFile));
        Specification specification = Specification.readSpec(spec);
        new DaoGenerator(specification, outputPath).generate();
    }

    public void generate() throws IOException {
        appendLine("public class %s {", specification.getName());
        appendLines(members.stream().map(this::memberDefinition));
        memberImports();
        append(new ConstructorNoArgsGenerator(specification));
        append(new ConstructorNonNullGenerator(specification));
        append(new ConstructorAllArgsGenerator(specification));
        append(new ConstructorResultSetGenerator(specification));
        append(new ValidationGenerator(specification));
        append(new GetterSetterGenerator(specification));
        append(new WithGenerator(specification));
        append(new DatabasePersistenceGenerator(specification));
        append(new DatabaseGetGenerator(specification));
        append(new JsonGenerator(specification));
        append(new CsvGenerator(specification));
        append(new BuilderGenerator(specification));
        append(new ToStringGenerator(specification));
        append(new HashEqualsGenerator(specification));
        append(getPrivateClasses());
        appendLine("}");
        writeSource(outputPath);
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

    private String memberDefinition(Member member) {
        return String.format("private%s%s %s;",
                member.isImmutable() ? " final " : " ",
                member.getType(),
                member.getName()
        );
    }

}
