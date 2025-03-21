package net.weichware.jbdao.spec.writer;

import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

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

    protected void append(String line) {
        code.append(line);
        indentIfNeeded(line);
    }

    protected void append(String format, Object... args) {
        append(new Formatter().format(format, args).toString());
    }

    protected void append(List<String> list) {
        for (String item : list) {
            code.append(item);
        }
    }

    protected void appendLine(String line) {
        outdentIfNeeded(line);

        code.append(getIndent());
        append(line);
        code.append("\n");

        indentIfNeeded(line);
    }

    protected void appendLine(String format, Object... args) {
        String line = new Formatter().format(format, args).toString();

        outdentIfNeeded(line);

        code.append(getIndent());
        code.append(line);
        code.append("\n");

        indentIfNeeded(line);
    }

    protected void appendLines(List<String> list) {
        for (String line : list) {
            appendLine(line);
        }
    }

    protected void appendLines(Stream<String> stream) {
        stream.forEach(this::appendLine);
    }

    protected void indent() {
        indent++;
    }

    protected void outdent() {
        indent--;
    }

    protected String getIndent() {
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

    private void indentIfNeeded(String line) {
        if (line.trim().endsWith("{")) {
            indent++;
        }
    }

    private void outdentIfNeeded(String line) {
        if (line.trim().endsWith("}")) {
            indent--;
        }
    }

}
