package net.weichware.jbdao.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public void writeSource(OutputStream outputStream) throws IOException {
        outputStream.write(getCode().getBytes());
    }

    public String getCode() {
        StringBuilder code = new StringBuilder();

        code.append("package ").append(packagePath).append(";\n\n");

        for (String importLine : getImports().stream().sorted().collect(toList())) {
            code.append("import ").append(importLine).append(";\n");
        }
        code.append("\n");

        code.append(super.getCode());

        return code.toString();
    }
}
