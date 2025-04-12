package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
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

        if (members.stream().anyMatch(Member::hasMasking)) {
            appendMaskedGetters();
        }

    }

    private void appendMaskedGetters() {
        members.stream().filter(Member::hasMasking).forEach(member -> {
            emptyLine();
            appendLine("public String get%sMasked(boolean nullable) {", NameUtil.firstCharacterUpper(member.getName()));
            appendLine("if (%s == null) {", member.getName());
            appendLine("return nullable ? null: \"\";");
            appendLine("}");
            if (member.getType().equals("String")) {
                appendLine("return %s.replaceAll(%s, %s);", member.getName(), quote(member.getMaskPattern()), quote(member.getMaskReplace()));
            } else {
                appendLine("return (%s + \"\").replaceAll(%s, %s);", member.getName(), quote(member.getMaskPattern()), quote(member.getMaskReplace()));
            }
            appendLine("}");
        });
    }

    private void appendToStringMember(Member member) {
        String comma = getComma();
        if (member.getType().equals("String")) {
            appendLine("\"%s%s='\" + %s + '\\'' +", comma, member.getName(), maskedCall(member));
        } else {
            appendLine("\"%s%s=\" + %s +", comma, member.getName(), maskedCall(member));
        }
    }

    private String maskedCall(Member member) {
        if (member.hasMasking()) {
            return "get" + NameUtil.firstCharacterUpper(member.getName()) + "Masked(true)";
        }
        return member.getName();
    }

    private String getComma() {
        if (isFirst) {
            isFirst = false;
            return "";
        }
        return ", ";
    }
}
