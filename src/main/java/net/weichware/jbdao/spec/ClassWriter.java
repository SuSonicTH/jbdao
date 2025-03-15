package net.weichware.jbdao.spec;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ClassWriter {
    protected static final HashMap<Integer, String> indentMap = new HashMap<>();
    protected final Set<String> importSet = new HashSet<>();
    protected final StringBuilder code = new StringBuilder();
    protected final String packagePath;
    protected final String name;

    ClassWriter(String packagePath, String name) {
        this.packagePath = packagePath;
        this.name = name;
    }

    protected void addImport(String... name) {
        importSet.addAll(Arrays.asList(name));
    }

    protected void appendLine() {
        code.append("\n");
    }

    protected String quote(String string) {
        return "\"" + string + "\"";
    }

    protected void appendLine(int indent, String... text) {
        appendLine(indent, Arrays.asList(text));
    }

    protected void appendLine(int indent, List<String> lines) {
        lines.stream()
                .filter(line -> !line.trim().isEmpty())
                .forEach(line -> code
                        .append(indent(indent))
                        .append(line)
                        .append("\n")
                );
    }

    protected void appendLineFormatted(int indent, String format, Object... args) {
        code
                .append(indent(indent))
                .append(String.format(format, args))
                .append("\n");
    }

    protected String indent(int indent) {
        return indentMap.computeIfAbsent(indent, k -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                stringBuilder.append("    ");
            }
            return stringBuilder.toString();
        });
    }

    protected void writeSource(Path basePath) throws IOException {
        Path outputPath = basePath.resolve(packagePath.replace(".", "/"));
        Path outputFile = outputPath.resolve(name + ".java");
        Files.createDirectories(outputPath);

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(outputFile)) {
            bufferedWriter.write("package " + packagePath + ";\n\n");
            for (String importLine : importSet.stream().sorted().collect(toList())) {
                bufferedWriter.write("import " + importLine + ";\n");
            }
            bufferedWriter.write("\n");
            bufferedWriter.write(code.toString());
        }
    }
}
