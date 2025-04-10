package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

import java.sql.Array;
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
        appendLine("public %s validate() {", specification.getName());
        members.forEach(member -> appendLines(getValidations(member)));
        appendLine("return this;");
        appendLine("}");
    }

    public static List<String> getValidations(Member member) {
        List<String> lines = new ArrayList<>();
        if (!member.isNullable() && !ClassUtil.primitiveToObjectMap.containsKey(member.getType())) {
            lines.add(String.format("if (%s == null) throw new ValidationException(\"%s may not be null\");", member.getName(), member.getName()));
        }
        if (member.nonEmpty()) {
            lines.add(String.format("if (%s.isEmpty()) throw new ValidationException(\"%s may not be empty\");", member.getName(), member.getName()));
        }
        if (member.getType().equals("String") && member.getPattern() != null) {
            lines.add(String.format("if (%s != null && !%s.matches(\"%s\")) throw new ValidationException(\"%s does not match pattern '%s'\");", member.getName(), member.getName(), member.getPattern(), member.getName(), member.getPattern()));
        }
        if (member.hasMinMax()) {
            if (member.getType().equals("String")) {
                if (member.getMin() != null) lines.add(String.format("if (%s != null && %s.length() < %s) throw new ValidationException(\"%s is shorter than min %s\");", member.getName(), member.getName(), member.getMin(), member.getName(), member.getMin()));
                if (member.getMax() != null) lines.add(String.format("if (%s != null && %s.length() > %s) throw new ValidationException(\"%s is longer than max %s\");", member.getName(), member.getName(), member.getMax(), member.getName(), member.getMax()));
            } else {
                if (member.getMin() != null) lines.add(String.format("if (%s < %s) throw new ValidationException(\"%s is lower then min %s\");", member.getName(), member.getMin(), member.getName(), member.getMin()));
                if (member.getMax() != null) lines.add(String.format("if (%s > %s) throw new ValidationException(\"%s is higher then max %s\");", member.getName(), member.getMax(), member.getName(), member.getMax()));
            }
        }
        return lines;
    }
}
