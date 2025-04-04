package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class NoArgsConstructor extends Generator {

    public NoArgsConstructor(Specification specification) {
        super(specification);

        if (specification.hasNoArgsConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("public %s() {", specification.getName());
        //todo: set defaults once implemented
        appendLine("}");
    }
}
