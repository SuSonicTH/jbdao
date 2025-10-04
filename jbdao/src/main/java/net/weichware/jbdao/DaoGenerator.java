package net.weichware.jbdao;

import net.weichware.jbdao.generator.BuilderGenerator;
import net.weichware.jbdao.generator.ConstructorAllArgsGenerator;
import net.weichware.jbdao.generator.ConstructorNoArgsGenerator;
import net.weichware.jbdao.generator.ConstructorNonNullGenerator;
import net.weichware.jbdao.generator.ConstructorResultSetGenerator;
import net.weichware.jbdao.generator.CsvGenerator;
import net.weichware.jbdao.generator.DatabaseGetGenerator;
import net.weichware.jbdao.generator.DatabasePersistenceGenerator;
import net.weichware.jbdao.generator.EnumGenerator;
import net.weichware.jbdao.generator.GetterSetterGenerator;
import net.weichware.jbdao.generator.GsonAdapterGenerator;
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
        super(specification.packagePath(), specification.name());
        this.specification = specification;
        this.members = specification.members();
        this.outputPath = outputPath;
    }

    public static void main(String[] args) {
        try {
            log.info("Starting codegen ");
            if (args.length != 3) {
                log.error("jbdao error: expecting exactly 3 arguments spec file/path,output path, and source path");
                System.exit(1);
            }

            Path specPath = Paths.get(args[0]);
            if (!Files.exists(specPath)) {
                log.error("error: spec file/path {} does not exist", specPath);
                System.exit(2);
            }

            Path outputPath = Paths.get(args[1]);
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            } else if (!Files.isDirectory(outputPath)) {
                log.error("error: output path {}} is not a directory", outputPath);
                System.exit(3);
            }

            Path sourcePath = Paths.get(args[2]);
            if (!Files.exists(sourcePath)) {
                Files.createDirectories(sourcePath);
            } else if (!Files.isDirectory(sourcePath)) {
                log.error("error: source output path {}} is not a directory", sourcePath);
                System.exit(4);
            }

            if (Files.isDirectory(specPath)) {
                for (Path file : getFileList(specPath)) {
                    generateClass(file, outputPath, sourcePath);
                }
            } else {
                generateClass(specPath, outputPath, sourcePath);
            }
        } catch (Exception e) {
            log.error("Error while executing codegen", e);
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

    private static void generateClass(Path specFile, Path outputPath, Path sourcePath) throws IOException {
        log.info("generating class for spec {}", specFile.getFileName());
        String spec = new String(Files.readAllBytes(specFile));
        Specification specification = Specification.readSpec(spec);
        new DaoGenerator(specification, outputPath).generate();
        if (specification.generateAbstract()) {
            new ConcreteClassGenerator(specification, sourcePath).generate();
        }
    }

    public void generate() throws IOException {
        if (specification.isEnum()) {
            appendLine("public enum %s {", specification.name());
            append(new EnumGenerator(specification));
        } else {
            if (specification.generateAbstract()) {
                appendLine("public abstract class Abstract%s<T extends %s> {", specification.name(), specification.name());
            } else {
                appendLine("public class %s {", specification.name());
            }
            generateDao();
        }
        appendLine("}");
        writeSource(specification.className(), outputPath);
    }

    private void generateDao() {
        append(new GsonAdapterGenerator(specification));
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
    }

    private void memberImports() {
        for (Member member : members) {
            if (!ClassUtil.javaBuildIn.contains(member.type())) {
                String clazz = ClassUtil.knownClasses.get(member.type());
                if (clazz != null) {
                    addImport(clazz);
                } else if (member.isEnum()) {
                    if (member.path() != null && !member.path().isEmpty()) {
                        addImport(member.path() + "." + member.type());
                    }
                } else {
                    throw new IllegalArgumentException("type '" + member.type() + " for member variable '" + member.name() + "' is unknown");
                }
            }
        }
    }

    private String memberDefinition(Member member) {
        return String.format("private%s%s %s;",
                member.isImmutable() ? " final " : " ",
                member.type(),
                member.name()
        );
    }

}
