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
        appendConstructorAssignment();
        appendLine("validate();");
        appendLine("}");
    }

    private String constructorArgumentList() {
        return members.stream()
                .filter(Member::isNotNullable)
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
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
