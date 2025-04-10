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
        appendLine("public %s get%s() {", member.getType(), NameUtil.firstCharacterUpper(member.getName()));
        appendLine("return %s;", member.getName());
        appendLine("}");

        if (!member.isImmutable()) {
            emptyLine();
            appendLine("public %s set%s(%s %s) {",
                    specification.getName(),
                    NameUtil.firstCharacterUpper(member.getName()),
                    member.getType(),
                    member.getName()
            );
            appendLines(ValidationGenerator.getValidations(member));
            appendLine("this.%s = %s;", member.getName(), member.getName());
            appendLine("return this;");
            appendLine("}");
        }
    }
}
