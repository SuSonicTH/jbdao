package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class GetterSetterGenerator extends Generator {
    public GetterSetterGenerator(Specification specification) {
        super(specification);

        members.stream()
                .filter(Member::generateGetter)
                .forEach(this::appendGetterSetter);
    }

    private void appendGetterSetter(Member member) {
        if (member.generateGetter()) {
            emptyLine();
            appendLine("public %s %s() {", member.type(), member.getterName());
            appendLine("return %s;", member.name());
            appendLine("}");
        }
        if (member.generateSetter()) {
            emptyLine();
            appendLine("public %s %s(%s %s) {",
                    specification.name(),
                    member.setterName(),
                    member.type(),
                    member.name()
            );
            appendLines(ValidationGenerator.getValidations(member));
            appendLine("this.%s = %s;", member.name(), member.name());
            appendLine(specification.returnThis());
            appendLine("}");
        }
    }
}
