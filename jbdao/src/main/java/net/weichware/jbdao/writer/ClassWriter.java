package net.weichware.jbdao.writer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClassWriter extends CodeWriter {
    protected final String packagePath;
    protected final String name;

    protected ClassWriter(String packagePath, String name) {
        this.packagePath = packagePath;
        this.name = name;
    }

    protected void writeSource(Path basePath) throws IOException {
        Path outputPath = basePath.resolve(packagePath.replace(".", "/"));
        Path outputFile = outputPath.resolve(name + ".java");
        Files.createDirectories(outputPath);

        Files.write(outputFile, getCode().getBytes());
    }

    public String getCode() {
        StringBuilder code = new StringBuilder();

        code.append("package ").append(packagePath).append(";\n");

        appendImport(code, getImports().stream().filter(i -> !i.startsWith("java")).sorted().collect(toList()));
        appendImport(code, getImports().stream().filter(i -> i.startsWith("java")).sorted().collect(toList()));

        code.append("\n");
        code.append(super.getCode());

        return code.toString();
    }

    private void appendImport(StringBuilder code, List<String> classes) {
        if (!classes.isEmpty()) {
            code.append("\n");
            for (String className : classes) {
                code.append("import ").append(className).append(";\n");
            }
        }
    }
}
