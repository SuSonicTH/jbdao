package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.NameUtil;
import net.weichware.jbdao.spec.Specification;
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
        eol();
        appendLine("public %s with%s(%s %s) {",
                specification.getName(),
                NameUtil.firstCharacterUpper(member.getName()),
                member.getType(),
                member.getName()
        );
        appendLine("return new %s(%s);", specification.getName(), argumentList());
        appendLine("}");
    }

    private String argumentList() {
        if (argumentList == null) {
            argumentList = members.stream()
                    .map(Member::getName)
                    .collect(joining(", "));
        }
        return argumentList;
    }
}
