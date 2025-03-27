package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

import java.util.stream.Collectors;

public class HashEqualsGenerator extends Generator {

    public HashEqualsGenerator(Specification specification) {
        super(specification);
        if (specification.generateHashEquals()) {
            addImport("java.util.Objects");
            appendEquals();
            appendHashCode();
        }
    }

    private void appendEquals() {
        final String className = specification.getName();
        final String objectName = NameUtil.firstCharacterLower(className);

        emptyLine();
        appendLine("@Override");
        appendLine("public boolean equals(Object o) {");
        appendLine("if (this == o) return true;");
        appendLine("if (!(o instanceof %s)) return false;", className);
        appendLine("%s %s = (%s) o;", className, objectName, className);
        String objectEqualsList = members.stream()
                .filter(Member::generateHashEquals)
                .map(Member::getName)
                .map(member -> "Objects.equals(" + member + ", " + objectName + "." + member + ")")
                .collect(Collectors.joining(" && "));
        appendLine("return " + objectEqualsList + ";");
        appendLine("}");
    }

    private void appendHashCode() {
        emptyLine();
        appendLine("@Override");
        appendLine("public int hashCode() {");
        String memberList = members.stream().filter(Member::generateHashEquals).map(Member::getName).collect(Collectors.joining(", "));
        appendLine("return Objects.hash(" + memberList + ");");
        appendLine("}");
    }

}
