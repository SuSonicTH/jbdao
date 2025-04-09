package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

import java.util.stream.Collectors;

public class BuilderGenerator extends Generator {
    public BuilderGenerator(Specification specification) {
        super(specification);
        if (specification.hasBuilder()) {
            appendBuilderMethod();
            addPrivateClass(new Builder(specification).getCode());
        }
    }

    private void appendBuilderMethod() {
        emptyLine();
        appendLine("public static Builder builder() {");
        appendLine("return new Builder();");
        appendLine("}");

        String arguments = members.stream().filter(Member::isNotNullable).map(Member::getName).collect(Collectors.joining(", "));
        emptyLine();
        appendLine("public static Builder builder(%s) {", getSignature());
        appendLine("return new Builder(%s);", arguments);
        appendLine("}");
    }

    private String getSignature() {
        return members.stream().filter(Member::isNotNullable).map(m -> m.getType() + " " + m.getName()).collect(Collectors.joining(", "));
    }

    private class Builder extends Generator {
        protected Builder(Specification specification) {
            super(specification);
            emptyLine();
            appendLine("public static class Builder {");
            appendMembers();
            appendConstructor();
            appendSetters();
            appendBuild();
            appendLine("}");
        }

        private void appendMembers() {
            members.forEach(member ->
                    appendLine("private %s %s%s;", member.getType(), member.getName(), member.getDefaultValue(" = "))
            );
        }

        private void appendConstructor() {
            emptyLine();
            appendLine("public Builder() {", getSignature());
            appendLine("}");

            emptyLine();
            appendLine("public Builder(%s) {", getSignature());
            members.stream().filter(Member::isNotNullable).forEach(member ->
                    appendLine("this.%s = %s;", member.getName(), member.getName())
            );
            appendLine("}");
        }

        private void appendSetters() {
            members.forEach(member -> {
                emptyLine();
                appendLine("public Builder set%s(%s %s) {", NameUtil.firstCharacterUpper(member.getName()), member.getType(), member.getName());
                appendLine("this.%s = %s;", member.getName(), member.getName());
                appendLine("return this;");
                appendLine("}");
            });
        }

        private void appendBuild() {
            emptyLine();
            appendLine("public %s build() {", specification.getName());
            appendLine("return new %s(%s);", specification.getName(), members.stream().map(Member::getName).collect(Collectors.joining(", ")));
            appendLine("}");
        }
    }
}
