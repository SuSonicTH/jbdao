package net.weichware.jbdao.spec.writer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CodeWriter {
    private static final HashMap<Integer, String> indentMap = new HashMap<>();
    private final StringBuilder code = new StringBuilder();
    private int indent;

    public CodeWriter(int indent) {
        this.indent = indent;
    }

    public CodeWriter() {
        this.indent = 0;
    }

    protected void eol() {
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

    public String getCode() {
        return code.toString();
    }
}
