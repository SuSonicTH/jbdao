package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

import static java.util.stream.Collectors.joining;

public class ConstructorAllArgsGenerator extends Generator {

    public ConstructorAllArgsGenerator(Specification specification) {
        super(specification);

        if (specification.hasAllArgsConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("%s %s(%s) {", specification.constructorVisibility(), specification.className(), constructorArgumentList());
        appendConstructorAssignment();
        appendLine("validate();");
        appendLine("}");
    }

    private String constructorArgumentList() {
        return members.stream()
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }



    private void appendConstructorAssignment() {
        appendLines(members.stream()
                .map(Member::getName)
                .map(name -> String.format("this.%s = %s;", name, name))
        );
    }

}
