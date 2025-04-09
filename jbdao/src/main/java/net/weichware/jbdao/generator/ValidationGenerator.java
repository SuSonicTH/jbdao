package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

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
        appendObjectsRequireNonNull();
        appendStringEmptyCheck();
        appendStringPatternCheck();
        appendMinMaxCheck();
        emptyLine();
        appendLine("return this;");
        appendLine("}");
    }

    private void appendObjectsRequireNonNull() {
        if (specification.hasNonNullable()) {
            addImport("java.util.Objects");
            appendLines(members.stream()
                    .filter(member -> !member.isNullable())
                    .filter(member -> !ClassUtil.primitiveToObjectMap.containsKey(member.getType()))
                    .map(Member::getName)
                    .map(name -> String.format("if (%s == null) throw new ValidationException(\"%s may not be null\");", name, name))
            );
            emptyLine();
        }
    }

    private void appendStringEmptyCheck() {
        if (specification.hasNonEmpty()) {
            appendLines(members.stream()
                    .filter(Member::nonEmpty)
                    .map(Member::getName)
                    .map(name -> "if (" + name + ".isEmpty()) throw new ValidationException(" + quote(name + " may not be empty") + ");")
            );
        }
    }

    private void appendStringPatternCheck() {
        if (specification.hasPatterns()) {
            emptyLine();
            members.stream()
                    .filter(member -> member.getPattern() != null)
                    .forEach(member -> appendLine("if (%s != null && !%s.matches(\"%s\")) throw new ValidationException(\"%s does not match pattern '%s'\");", member.getName(), member.getName(), member.getPattern(), member.getName(), member.getPattern()));
        }
    }

    private void appendMinMaxCheck() {
        if (specification.hasMinMax()) {
            emptyLine();
            members.stream().filter(Member::hasMinMax)
                    .forEach(member -> {
                        if (member.getType().equals("String")) {
                            if (member.getMin() != null) appendLine("if (%s!= null && %s.length() < %s) throw new ValidationException(\"%s is shorter than min %s\");", member.getName(), member.getName(), member.getMin(), member.getName(), member.getMin());
                            if (member.getMax() != null) appendLine("if (%s!= null && %s.length() > %s) throw new ValidationException(\"%s is longer than max %s\");", member.getName(), member.getName(), member.getMax(), member.getName(), member.getMax());
                        } else {
                            if (member.getMin() != null) appendLine("if (%s < %s) throw new ValidationException(\"%s is lower then min %s\");", member.getName(), member.getMin(), member.getName(), member.getMin());
                            if (member.getMax() != null) appendLine("if (%s > %s) throw new ValidationException(\"%s is higher then max %s\");", member.getName(), member.getMax(), member.getName(), member.getMax());
                        }
                    });
        }
    }

}
