package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

public class ConstructorNoArgsGenerator extends Generator {

    public ConstructorNoArgsGenerator(Specification specification) {
        super(specification);

        if (specification.hasNoArgsConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("%s %s() {", specification.constructorVisibility(), specification.className());
        for (Member member : members) {
            if (member.hasDefault()) {
                appendLine("%s = %s;", member.name(), member.defaultValue());
            } else if (member.type().equals("boolean")) {
                appendLine("%s = false;", member.name());
            } else if (ClassUtil.primitiveToObjectMap.get(member.type()) != null) {
                appendLine("%s = 0;", member.name());
            } else {
                appendLine("%s = null;", member.name());
            }
        }
        appendLine("}");
    }
}
