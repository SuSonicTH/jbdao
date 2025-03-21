package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.NameUtil;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class GetterGenerator extends Generator {
    public GetterGenerator(Specification specification) {
        super(specification);

        members.stream()
                .filter(Member::generateGetter)
                .forEach(this::appendGetter);
    }

    private void appendGetter(Member member) {
        emptyLine();
        appendLine("public %s get%s() {",
                member.getType(),
                NameUtil.firstCharacterUpper(member.getName())
        );
        appendLine("return %s;", member.getName());
        appendLine("}");
    }
}
