package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.NameUtil;
import net.weichware.jbdao.writer.Generator;

import static java.util.stream.Collectors.joining;

public class WithGenerator extends Generator {
    private String argumentList;

    public WithGenerator(Specification specification) {
        super(specification);

        if (specification.generateWith()) {
            members.stream()
                    .filter(Member::generateWith)
                    .forEach(this::appendWith);
        }
    }

    private void appendWith(Member member) {
        emptyLine();
        appendLine("public %s with%s(%s %s) {",
                specification.name(),
                NameUtil.firstCharacterUpper(member.name()),
                member.type(),
                member.name()
        );
        appendLine("return new %s(%s);", specification.name(), argumentList());
        appendLine("}");
    }

    private String argumentList() {
        if (argumentList == null) {
            argumentList = members.stream()
                    .map(Member::name)
                    .collect(joining(", "));
        }
        return argumentList;
    }
}
