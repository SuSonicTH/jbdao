package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

import static java.util.stream.Collectors.joining;

public class ConstructorNonNullGenerator extends Generator {

    public ConstructorNonNullGenerator(Specification specification) {
        super(specification);

        if (specification.hasNonNullConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("%s %s(%s) {", specification.constructorVisibility(), specification.className(), constructorArgumentList());
        appendConstructorAssignment();
        appendLine("validate();");
        appendLine("}");
    }

    private String constructorArgumentList() {
        return members.stream()
                .filter(Member::isNotNullable)
                .map(member -> member.type() + " " + member.name())
                .collect(joining(", "));
    }

    private void appendConstructorAssignment() {
        for (Member member : members) {
            if (member.isNotNullable()) {
                appendLine("this.%s = %s;", member.name(), member.name());
            } else if (member.hasDefault()) {
                appendLine("%s = %s;", member.name(), member.defaultValue());
            } else if (member.type().equals("boolean")) {
                appendLine("%s = false;", member.name());
            } else if (ClassUtil.primitiveToObjectMap.get(member.type()) != null) {
                appendLine("%s = 0;", member.name());
                break;
            } else {
                appendLine("%s = null;", member.name());
            }
        }
    }

}
