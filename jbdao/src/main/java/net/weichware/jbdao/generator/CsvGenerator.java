package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

import java.util.stream.Collectors;

public class CsvGenerator extends Generator {
    public CsvGenerator(Specification specification) {
        super(specification);
        if (specification.hasCsv()) {
            addImport("java.util.Map");
            appendStreamCsv();
            addPrivateClass(new CsvReaderGenerator(specification));
            addExtraClass("AbstractCsvReader.java");
            addImport("net.weichware.jbdao.AbstractCsvReader");
            addExtraClass("CsvReaderException.java");
            addImport("net.weichware.jbdao.CsvReaderException");
        }
    }

    private void appendStreamCsv() {
        emptyLine();
        appendLine("public static Stream<%s> streamCsv(Path file) {", specification.name());
        appendLine("try {");
        appendLine("return StreamSupport.stream(new CsvReader(file, true), false);");
        appendLine("} catch (IOException e) {");
        appendLine("throw new CsvReaderException(\"Could not read file '\" + file + \"'\", e);");
        appendLine("}");
        appendLine("}");

    }

    private class CsvReaderGenerator extends Generator {
        public CsvReaderGenerator(Specification specification) {
            super(specification);
            emptyLine();
            appendLine("private static class CsvReader extends AbstractCsvReader<%s> {", specification.name());
            members.stream().filter(Member::hasCsv)
                    .forEach(member -> appendLine("private int %s;", member.name()));

            emptyLine();
            appendLine("public CsvReader(Path file, boolean hasHeader) throws IOException {");
            appendLine("super(Files.newBufferedReader(file), hasHeader);");
            appendLine("}");

            emptyLine();
            appendLine("@Override");
            appendLine("protected void validateHeader(Map<String, Integer> header) {");
            members.stream().filter(Member::hasCsv)
                    .forEach(member -> appendLine("%s = header.get(\"%s\");", member.name(), member.csvName()));
            appendLine("}");

            emptyLine();
            appendLine("@Override");
            appendLine("protected %s create(List<String> fields) {", specification.name());
            String arguments = members.stream().filter(Member::hasCsv)
                    .map(this::argument)
                    .collect(Collectors.joining(", "));
            appendLine("return new %s(%s);", specification.name(), arguments);
            appendLine("}");

            appendLine("}");
        }

        private String argument(Member member) {
            if (member.type().equals("String")) {
                return String.format("fields.get(%s)", member.name());
            } else if (member.isEnum()) {
                return String.format("%s.fromCsv(fields.get(%s))", member.type(), member.name());
            } else if (member.type().equals("Long") || member.type().equals("long")) {
                return String.format("Long.parseLong(fields.get(%s))", member.name());
            } else if (member.type().equals("Integer") || member.type().equals("int")) {
                return String.format("Integer.parseInt(fields.get(%s))", member.name());
            } else {
                throw new RuntimeException("Csv reading of type " + member.type() + " is not implemented");
            }
        }

    }

}
