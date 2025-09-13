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
        appendLine("return \"" + specification.name() + "{\" +");
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
            appendLine("public String get%sMasked(boolean nullable) {", NameUtil.firstCharacterUpper(member.name()));
            appendLine("if (%s == null) {", member.name());
            appendLine("return nullable ? null : \"\";");
            appendLine("}");
            if (member.type().equals("String")) {
                appendLine("return %s.replaceAll(%s, %s);", member.name(), quote(member.maskPattern()), quote(member.maskReplace()));
            } else {
                appendLine("return (%s + \"\").replaceAll(%s, %s);", member.name(), quote(member.maskPattern()), quote(member.maskReplace()));
            }
            appendLine("}");
        });
    }

    private void appendToStringMember(Member member) {
        String comma = getComma();
        if (member.type().equals("String")) {
            appendLine("\"%s%s='\" + %s + '\\'' +", comma, member.name(), maskedCall(member));
        } else {
            appendLine("\"%s%s=\" + %s +", comma, member.name(), maskedCall(member));
        }
    }

    private String maskedCall(Member member) {
        if (member.hasMasking()) {
            return "get" + NameUtil.firstCharacterUpper(member.name()) + "Masked(true)";
        }
        return member.name();
    }

    private String getComma() {
        if (isFirst) {
            isFirst = false;
            return "";
        }
        return ", ";
    }
}
