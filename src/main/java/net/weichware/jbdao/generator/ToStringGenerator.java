package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class ToStringGenerator extends Generator {
    private boolean isFirst = true;

    public ToStringGenerator(Specification specification) {
        super(specification);
        if (specification.generateToString()) {
            appendToString();
        }
    }

    private void appendToString() {
        emptyLine();
        appendLine("@Override");
        appendLine("public String toString() {");
        appendLine("return \"" + specification.getName() + "{\" +");
        indent(2);
        members.stream()
                .filter(Member::generateToString)
                .forEach(this::appendToStringMember);
        appendLine("'}';");
        outdent(2);
        appendLine("}");
    }

    private void appendToStringMember(Member member) {
        String comma = getComma();
        if (member.getType().equals("String")) {
            appendLine("\"%s%s='\" + %s + '\\'' +", comma, member.getName(), member.getName());
        } else {
            appendLine("\"%s%s=\" + %s +", comma, member.getName(), member.getName());
        }
    }

    private String getComma() {
        if (isFirst) {
            isFirst = false;
            return "";
        }
        return ", ";
    }
}
