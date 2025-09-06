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
        appendLine("public %s() {", specification.getName());
        for (Member member : members) {
            if (member.hasDefault()) {
                appendLine("%s = %s;", member.getName(), member.getDefaultValue());
            } else if (member.getType().equals("boolean")) {
                appendLine("%s = false;", member.getName());
            } else if (ClassUtil.primitiveToObjectMap.get(member.getType()) != null) {
                appendLine("%s = 0;", member.getName());
            } else {
                appendLine("%s = null;", member.getName());
            }
        }
        appendLine("}");
    }
}
