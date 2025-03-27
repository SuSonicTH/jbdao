package net.weichware.jbdao.writer;

import java.util.*;
import java.util.stream.Stream;

public class CodeWriter {
    private static final HashMap<Integer, String> indentMap = new HashMap<>();
    private final Set<String> importSet = new HashSet<>();
    private final StringBuilder code = new StringBuilder();
    private int indent;

    protected CodeWriter(int indent) {
        this.indent = indent;
    }

    protected CodeWriter() {
        this.indent = 0;
    }

    public String getCode() {
        return code.toString();
    }

    public Set<String> getImports() {
        return importSet;
    }

    protected void addImport(String... clazz) {
        importSet.addAll(Arrays.asList(clazz));
    }

    protected void emptyLine() {
        code.append("\n");
    }

    protected String quote(String string) {
        return "\"" + string + "\"";
    }

    protected void append(CodeWriter codeWriter) {
        importSet.addAll(codeWriter.importSet);
        code.append(codeWriter.getCode());
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

    protected void indent(int level) {
        indent += level;
    }

    protected void outdent() {
        indent--;
    }

    protected void outdent(int level) {
        indent -= level;
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
