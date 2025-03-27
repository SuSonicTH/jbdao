package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

public class DatabaseGetGenerator extends Generator {

    protected DatabaseGetGenerator(Specification specification) {
        super(specification);

        if (specification.generateDatabase()) {
            appendGetList();
        }
    }

    private void appendGetList() {

    }

}
