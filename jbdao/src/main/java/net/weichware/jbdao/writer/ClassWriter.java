package net.weichware.jbdao.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ClassWriter extends CodeWriter {
    private static final Logger log = LoggerFactory.getLogger(ClassWriter.class);
    private static final HashSet<String> extraClasses = new HashSet<>();
    protected final String packagePath;
    protected final String name;

    protected ClassWriter(String packagePath, String name) {
        this.packagePath = packagePath;
        this.name = name;
    }

    protected void writeSource(String className, Path basePath) throws IOException {
        writeSource(basePath, packagePath, className + ".java", getCode());
        getExtraClassSet().forEach(fileName -> writeExtraClass(basePath, fileName));
    }

    protected Path getOutputFilePath(Path basePath, String packageName, String className) {
        return basePath
                .resolve(packagePath.replace(".", "/"))
                .resolve(className + ".java");
    }
    private void writeSource(Path basePath, String packageName, String fileName, String source) throws IOException {
        Path outputPath = basePath.resolve(packagePath.replace(".", "/"));
        Path outputFile = outputPath.resolve(fileName);
        Files.createDirectories(outputPath);
        Files.write(outputFile, source.getBytes());
    }

    private void writeExtraClass(Path basePath, String fileName) {
        if (!extraClasses.contains(fileName)) {
            log.info("adding class {}", fileName);
            try {
                String source = getResourceFileAsString(fileName);
                String packageName = Arrays.stream(source.split("\r?\n"))
                        .map(String::trim)
                        .filter(line -> line.startsWith("package"))
                        .findFirst()
                        .orElseThrow(() -> new DaoGeneratorException("Package not found for utility class " + fileName));
                writeSource(basePath, packageName, fileName, source);
                extraClasses.add(fileName);
            } catch (IOException e) {
                throw new DaoGeneratorException("Could not write support class " + fileName, e);
            }
        }
    }

    private String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassWriter.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Could not load resource " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
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

    private static class DaoGeneratorException extends RuntimeException {
        public DaoGeneratorException(String message, IOException exception) {
            super(message, exception);
        }

        public DaoGeneratorException(String message) {
            super(message);
        }
    }
}
