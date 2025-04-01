package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class JsonGenerator extends Generator {
    public JsonGenerator(Specification specification) {
        super(specification);
        if (specification.generateJson()) {
            addImports();
            appendFromJson();
            appendToJson();
        }
    }

    private void addImports() {
        addImport(
                "java.io.IOException",
                "java.io.InputStream",
                "java.io.InputStreamReader",
                "java.io.OutputStream",
                "java.io.Reader",
                "java.io.Writer",
                "java.nio.charset.StandardCharsets",
                "java.nio.file.Files",
                "java.nio.file.Path",
                "com.google.gson.Gson"
        );
    }

    private void appendFromJson() {
        String className = specification.getName();

        emptyLine();
        appendLine("public static %s fromJson(String json) {", className);
        appendLine("return new Gson().fromJson(json, %s.class);", className);
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(Reader jsonReader) {", className);
        appendLine("return new Gson().fromJson(jsonReader, %s.class);", className);
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(InputStream jsonStream) throws IOException {", className);
        appendLine("try (Reader jsonReader = new InputStreamReader(jsonStream)) {");
        appendLine("return new Gson().fromJson(jsonReader, %s.class);", className);
        appendLine("}");
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(Path jsonFile) throws IOException {", className);
        appendLine("try (Reader jsonReader = new InputStreamReader(Files.newInputStream(jsonFile))) {");
        appendLine("return new Gson().fromJson(jsonReader, %s.class);", className);
        appendLine("}");
        appendLine("}");
    }

    private void appendToJson() {
        emptyLine();
        appendLine("public String toJson() {");
        appendLine("return new Gson().toJson(this);");
        appendLine("}");

        emptyLine();
        appendLine("public void writeJson(Writer writer) throws IOException {");
        appendLine("writer.write(toJson());");
        appendLine("}");

        emptyLine();
        appendLine("public void writeJson(OutputStream outputStream) throws IOException {");
        appendLine("outputStream.write(toJson().getBytes(StandardCharsets.UTF_8));");
        appendLine("}");
    }

}
