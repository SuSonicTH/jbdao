package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

import java.util.ArrayList;
import java.util.List;

public class ValidationGenerator extends Generator {
    public ValidationGenerator(Specification specification) {
        super(specification);
        if (specification.needsValidation()) {
            appendValidation();
        }
    }

    private void appendValidation() {
        addExtraClass("ValidationException.java");
        addImport("net.weichware.jbdao.ValidationException");

        emptyLine();
        appendLine("public %s validate() {", specification.returnThisType());
        members.forEach(member -> appendLines(getValidations(member)));
        appendLine(specification.returnThis());
        appendLine("}");
    }

    public static List<String> getValidations(Member member) {
        List<String> lines = new ArrayList<>();
        if (!member.isNullable() && !ClassUtil.primitiveToObjectMap.containsKey(member.type())) {
            lines.add(String.format("if (%s == null) throw new ValidationException(\"%s may not be null\");", member.name(), member.name()));
        }
        if (member.nonEmpty()) {
            lines.add(String.format("if (%s.isEmpty()) throw new ValidationException(\"%s may not be empty\");", member.name(), member.name()));
        }
        if (member.type().equals("String") && member.pattern() != null) {
            lines.add(String.format("if (%s != null && !%s.matches(\"%s\")) throw new ValidationException(\"%s does not match pattern '%s'\");", member.name(), member.name(), member.pattern(), member.name(), member.pattern()));
        }
        if (member.hasMinMax()) {
            if (member.type().equals("String")) {
                if (member.min() != null) lines.add(String.format("if (%s != null && %s.length() < %s) throw new ValidationException(\"%s is shorter than min %s\");", member.name(), member.name(), member.min(), member.name(), member.min()));
                if (member.max() != null) lines.add(String.format("if (%s != null && %s.length() > %s) throw new ValidationException(\"%s is longer than max %s\");", member.name(), member.name(), member.max(), member.name(), member.max()));
            } else if (member.type().equals("LocalDate")) {
                if (member.min() != null) {
                    if (member.min().matches("\\d\\d\\d\\d/\\d\\d/\\d\\d")) {
                        lines.add(String.format("if (%s != null && %s.isBefore(LocalDate.parse(\"%s\"))) throw new ValidationException(\"%s is before %s\");", member.name(), member.name(), member.min(), member.name(), member.min()));
                    } else {
                        lines.add(String.format("if (%s != null && %s.isBefore(%s)) throw new ValidationException(\"%s is before %s\");", member.name(), member.name(), member.min(), member.name(), member.min()));
                    }
                }
                if (member.max() != null) {
                    if (member.max().matches("\\d\\d\\d\\d/\\d/\\d")) {
                        lines.add(String.format("if (%s != null && %s.isAfter(LocalDate.parse(\"%s\")) throw new ValidationException(\"%s is after %s\");", member.name(), member.name(), member.max(), member.name(), member.max()));
                    } else {
                        lines.add(String.format("if (%s != null && %s.isAfter(%s)) throw new ValidationException(\"%s is after %s\");", member.name(), member.name(), member.max(), member.name(), member.max()));
                    }
                }
            } else if (member.type().equals("LocalDateTime")) {
                if (member.min() != null) {
                    if (member.min().matches("\\d\\d\\d\\d/\\d\\d/\\d\\d \\d\\d:\\d\\d:\\d\\d")) {
                        lines.add(String.format("if (%s != null && %s.isBefore(LocalDateTime.parse(\"%s\"))) throw new ValidationException(\"%s is before %s\");", member.name(), member.name(), member.min(), member.name(), member.min()));
                    } else {
                        lines.add(String.format("if (%s != null && %s.isBefore(%s)) throw new ValidationException(\"%s is before %s\");", member.name(), member.name(), member.min(), member.name(), member.min()));
                    }
                }
                if (member.max() != null) {
                    if (member.max().matches("\\d\\d\\d\\d/\\d/\\d \\d\\d:\\d\\d:\\d\\d")) {
                        lines.add(String.format("if (%s != null && %s.isAfter(LocalDateTime.parse(\"%s\")) throw new ValidationException(\"%s is after %s\");", member.name(), member.name(), member.max(), member.name(), member.max()));
                    } else {
                        lines.add(String.format("if (%s != null && %s.isAfter(%s)) throw new ValidationException(\"%s is after %s\");", member.name(), member.name(), member.max(), member.name(), member.max()));
                    }
                }
            } else {
                if (member.min() != null) lines.add(String.format("if (%s < %s) throw new ValidationException(\"%s is lower then min %s\");", member.name(), member.min(), member.name(), member.min()));
                if (member.max() != null) lines.add(String.format("if (%s > %s) throw new ValidationException(\"%s is higher then max %s\");", member.name(), member.max(), member.name(), member.max()));
            }
        }
        return lines;
    }
}
