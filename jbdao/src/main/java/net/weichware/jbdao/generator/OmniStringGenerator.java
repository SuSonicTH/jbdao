package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

import java.util.stream.Collectors;

public class OmniStringGenerator extends Generator {
    public OmniStringGenerator(Specification specification) {
        super(specification);
        if (specification.generateOmiString()) {
            appendToString();
        }
    }

    private void appendToString() {
        emptyLine();
        appendLine("public String toOmniString() {");
        appendLine("return String.join(\" \",");
        indent(2);
        append(getIndent() + members.stream()
                .map(member -> member.name() + (member.type().equals("String") ? "" : " + \"\""))
                .collect(Collectors.joining(",\n" + getIndent()))+"\n") ;
        outdent(2);
        appendLine(").toLowerCase();");
        appendLine("}");
    }

}
