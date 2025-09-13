package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

public class GetterSetterGenerator extends Generator {
    public GetterSetterGenerator(Specification specification) {
        super(specification);

        members.stream()
                .filter(Member::generateGetter)
                .forEach(this::appendGetterSetter);
    }

    private void appendGetterSetter(Member member) {
        emptyLine();
        appendLine("public %s get%s() {", member.type(), NameUtil.firstCharacterUpper(member.name()));
        appendLine("return %s;", member.name());
        appendLine("}");

        if (!member.isImmutable()) {
            emptyLine();
            appendLine("public %s set%s(%s %s) {",
                    specification.name(),
                    NameUtil.firstCharacterUpper(member.name()),
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
