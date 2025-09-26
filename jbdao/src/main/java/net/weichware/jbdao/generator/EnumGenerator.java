package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

import java.util.StringJoiner;
import java.util.function.Consumer;

public class EnumGenerator extends Generator {

    public EnumGenerator(Specification specification) {
        super(specification);
        addImport("net.weichware.jbdao.ValidationException",
                "java.util.Arrays",
                "java.util.Map",
                "java.util.Optional",
                "java.util.stream.Collectors"
        );
        appendInit();

        emptyLine();
        appendGenerator(type -> appendLine("private static final Map<String, %s> %sMap = Arrays.stream(values()).collect(Collectors.toMap(k -> k.%s, v -> v));", specification.className(), type, type));

        emptyLine();
        appendGenerator(type -> appendLine("private final String %s;", type));

        appendConstructor();
        appendGenerator(this::appendMapperSub);
        appendGenerator(this::appendOptionalMapperSub);
        appendGenerator(this::appendGetterSub);

        if (specification.generateJson()) {
            append(new EnumGenerator.GsonAdapterGenerator(specification));
        }
    }

    private String constructorArguments() {
        emptyLine();
        StringJoiner joiner = new StringJoiner(", ");
        if (specification.generateDatabase()) {
            joiner.add("String database");
        }
        if (specification.generateCsv()) {
            joiner.add("String csv");
        }
        if (specification.generateJson()) {
            joiner.add("String json");
        }
        return joiner.toString();
    }

    private void appendInit() {
        for (int i = 0; i < members.size(); i++) {
            appendLine("%s(%s)%c", members.get(i).name(), enumArguments(members.get(i)), (i < members.size() - 1 ? ',' : ';'));
        }
    }

    private String enumArguments(Member member) {
        StringJoiner joiner = new StringJoiner(", ");
        if (specification.generateDatabase()) {
            joiner.add(quote(member.databaseName()));
        }
        if (specification.generateCsv()) {
            joiner.add(quote(member.csvName()));
        }
        if (specification.generateJson()) {
            joiner.add(quote(member.jsonName()));
        }
        return joiner.toString();
    }

    private void appendGenerator(Consumer<String> generator) {
        if (specification.generateDatabase()) {
            generator.accept("database");
        }
        if (specification.generateCsv()) {
            generator.accept("csv");
        }
        if (specification.generateJson()) {
            generator.accept("json");
        }
    }

    private void appendConstructor() {
        appendLine("%s(%s) {", specification.className(), constructorArguments());
        appendGenerator(type -> appendLine("this.%s = %s;", type, type));
        appendLine("}");
    }

    private void appendGetterSub(String type) {
        emptyLine();
        appendLine("public String to%s() {", NameUtil.firstCharacterUpper(type));
        appendLine("return %s;", type);
        appendLine("}");
    }

    private void appendMapperSub(String type) {
        emptyLine();
        appendLine("public static %s from%s(String value) {", specification.className(), NameUtil.firstCharacterUpper(type));
        appendLine("return optionalFrom%s(value).orElseThrow(() -> new ValidationException(\"%s value '\" + value + \"' for enum %s is unknown\"));", NameUtil.firstCharacterUpper(type), type, specification.className());
        appendLine("}");
    }

    private void appendOptionalMapperSub(String type) {
        emptyLine();
        appendLine("public static Optional<%s> optionalFrom%s(String value) {", specification.className(), NameUtil.firstCharacterUpper(type));
        appendLine("return Optional.ofNullable(%sMap.get(value));", type);
        appendLine("}");
    }

    public class GsonAdapterGenerator extends Generator {
        public GsonAdapterGenerator(Specification specification) {
            super(specification);
            addImport(
                    "com.google.gson.TypeAdapter",
                    "com.google.gson.stream.JsonReader",
                    "com.google.gson.stream.JsonWriter",
                    "java.io.IOException"
            );
            String className = specification.className();
            emptyLine();
            appendLine("public static class GsonAdapter extends TypeAdapter<%s> {", className);
            appendLine("@Override");
            appendLine("public void write(final JsonWriter jsonWriter, final %s %s) throws IOException {", className, NameUtil.firstCharacterLower(className));
            appendLine("jsonWriter.value(product.toJson());");
            appendLine("}");
            emptyLine();
            appendLine("@Override");
            appendLine("public %s read(final JsonReader jsonReader) throws IOException {", className);
            appendLine("return %s.fromJson(jsonReader.nextString());", className);
            appendLine("}");
            appendLine("}");
        }
    }
}
