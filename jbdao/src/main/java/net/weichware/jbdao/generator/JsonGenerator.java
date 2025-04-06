package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
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
                "net.weichware.jbdao.GsonUtil"
        );
    }

    private void appendFromJson() {
        String className = specification.getName();
        String setDefaultPrefix = "";
        String setDefaultSuffix = "";
        if (dasDefaults()) {
            setDefaultPrefix = "setDefaults(";
            setDefaultSuffix = ")";
        }
        emptyLine();
        appendLine("public static %s fromJson(String json) {", className);
        appendLine("return %sGsonUtil.gson.fromJson(json, %s.class)%s;", setDefaultPrefix, className, setDefaultSuffix);
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(Reader jsonReader) {", className);
        appendLine("return %sGsonUtil.gson.fromJson(jsonReader, %s.class)%s;", setDefaultPrefix, className, setDefaultSuffix);
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(InputStream jsonStream) throws IOException {", className);
        appendLine("try (Reader jsonReader = new InputStreamReader(jsonStream)) {");
        appendLine("return %sGsonUtil.gson.fromJson(jsonReader, %s.class)%s;", setDefaultPrefix, className, setDefaultSuffix);
        appendLine("}");
        appendLine("}");

        emptyLine();
        appendLine("public static %s fromJson(Path jsonFile) throws IOException {", className);
        appendLine("try (Reader jsonReader = new InputStreamReader(Files.newInputStream(jsonFile))) {");
        appendLine("return %sGsonUtil.gson.fromJson(jsonReader, %s.class)%s;", setDefaultPrefix, className, setDefaultSuffix);
        appendLine("}");
        appendLine("}");

        if (dasDefaults()) {
            emptyLine();
            String varName = NameUtil.firstCharacterLower(specification.getName());
            appendLine("private static %s setDefaults(%s %s) {", className, className, varName);
            members.stream().filter(Member::hasDefault).forEach(member -> {
                appendLine("if (%s.%s == null) {", varName, member.getName());
                appendLine("%s = %s.with%s(%s);", varName, varName, NameUtil.firstCharacterUpper(member.getName()), member.getDefaultValue());
                appendLine("}");
            });
            appendLine("return %s;", varName);
            appendLine("}");
        }
    }

    private void appendToJson() {
        emptyLine();
        appendLine("public String toJson() {");
        appendLine("return GsonUtil.gson.toJson(this);");
        appendLine("}");

        emptyLine();
        appendLine("public void toJson(Writer writer) throws IOException {");
        appendLine("writer.write(toJson());");
        appendLine("}");

        emptyLine();
        appendLine("public void toJson(OutputStream outputStream) throws IOException {");
        appendLine("outputStream.write(toJson().getBytes(StandardCharsets.UTF_8));");
        appendLine("}");

        emptyLine();
        appendLine("public void toJson(Path jsonFile) throws IOException {");
        appendLine("Files.write(jsonFile, toJson().getBytes(StandardCharsets.UTF_8));");
        appendLine("}");
    }

    private boolean dasDefaults() {
        return members.stream().anyMatch(Member::hasDefault);
    }
}
