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

        String arguments = members.stream().filter(Member::isNotNullable).map(Member::name).collect(Collectors.joining(", "));
        emptyLine();
        appendLine("public static Builder builder(%s) {", getNonNullableSignature());
        appendLine("return new Builder(%s);", arguments);
        appendLine("}");

        String allArguments = members.stream().map(Member::name).collect(Collectors.joining(", "));
        emptyLine();
        appendLine("public Builder builderFrom() {");
        appendLine("return new Builder(%s);", allArguments);
        appendLine("}");

    }

    private String getNonNullableSignature() {
        return members.stream().filter(Member::isNotNullable).map(m -> m.type() + " " + m.name()).collect(Collectors.joining(", "));
    }

    private String getAllMembersSignature() {
        return members.stream().map(m -> m.type() + " " + m.name()).collect(Collectors.joining(", "));
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
                    appendLine("private %s %s%s;", member.type(), member.name(), member.defaultValue(" = "))
            );
        }

        private void appendConstructor() {
            emptyLine();
            appendLine("public Builder() {", getNonNullableSignature());
            appendLine("}");

            emptyLine();
            appendLine("public Builder(%s) {", getNonNullableSignature());
            members.stream().filter(Member::isNotNullable).forEach(member ->
                    appendLine("this.%s = %s;", member.name(), member.name())
            );
            appendLine("}");

            emptyLine();
            String allArguments = members.stream().map(Member::name).collect(Collectors.joining(", "));
            appendLine("public Builder(%s) {", getAllMembersSignature());
            members.forEach(member ->
                    appendLine("this.%s = %s;", member.name(), member.name())
            );
            appendLine("}");
        }

        private void appendSetters() {
            members.forEach(member -> {
                emptyLine();
                appendLine("public Builder set%s(%s %s) {", NameUtil.firstCharacterUpper(member.name()), member.type(), member.name());
                appendLine("this.%s = %s;", member.name(), member.name());
                appendLine("return this;");
                appendLine("}");
            });
        }

        private void appendBuild() {
            emptyLine();
            appendLine("public %s build() {", specification.name());
            appendLine("return new %s(%s);", specification.name(), members.stream().map(Member::name).collect(Collectors.joining(", ")));
            appendLine("}");
        }
    }
}
