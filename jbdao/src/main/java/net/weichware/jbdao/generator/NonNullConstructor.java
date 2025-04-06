package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

import static java.util.stream.Collectors.joining;

public class NonNullConstructor extends Generator {

    public NonNullConstructor(Specification specification) {
        super(specification);

        if (specification.hasNonNullConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("public %s(%s) {", specification.getName(), constructorArgumentList());
        appendObjectsRequireNonNull();
        appendStringEmptyCheck();
        appendConstructorAssignment();
        appendLine("}");
    }

    private String constructorArgumentList() {
        return members.stream()
                .filter(Member::isNotNullable)
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

    private void appendObjectsRequireNonNull() {
        if (specification.hasNonNullable()) {
            addImport("java.util.Objects");
            appendLines(members.stream()
                    .filter(member -> !member.isNullable())
                    .filter(member -> !ClassUtil.primitiveToObjectMap.containsKey(member.getType()))
                    .map(Member::getName)
                    .map(name -> String.format("Objects.requireNonNull(%s, \"%s may not be null\");", name, name))
            );
            emptyLine();
        }
    }

    private void appendStringEmptyCheck() {
        if (specification.hasNonEmpty()) {
            appendLines(members.stream()
                    .filter(member -> member.getType().equals("String"))
                    .filter(Member::acceptsEmpty)
                    .map(Member::getName)
                    .map(name -> "if (" + name + ".isEmpty()) throw new IllegalArgumentException(" + quote(name + " may not be empty") + ");")
            );
            emptyLine();
        }
    }

    private void appendConstructorAssignment() {
        for (Member member : members) {
            if (member.isNotNullable()) {
                appendLine("this.%s = %s;", member.getName(), member.getName());
            } else if (member.hasDefault()) {
                appendLine("%s = %s;", member.getName(), member.getDefaultValue());
            } else if (member.getType().equals("boolean")) {
                appendLine("%s = false;", member.getName());
            } else if (ClassUtil.primitiveToObjectMap.get(member.getType()) != null) {
                appendLine("%s = 0;", member.getName());
                break;
            } else {
                appendLine("%s = null;", member.getName());
            }
        }
    }

}
