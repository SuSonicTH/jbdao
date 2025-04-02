package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.util.ClassUtil;
import net.weichware.jbdao.writer.Generator;

import static java.util.stream.Collectors.joining;

public class AllArgsConstructor extends Generator {

    public AllArgsConstructor(Specification specification) {
        super(specification);

        if (specification.hasAllArgsConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        emptyLine();
        appendLine("public %s(%s) {", specification.getName(), constructorArgumentList());
        appendObjectsRequireNonNull();
        appendStringEmptyCheck();
        appendConstructorAssignment();
        appendLine("}");
    }

    private String constructorArgumentList() {
        return members.stream()
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

    private void appendObjectsRequireNonNull() {
        if (specification.hasNonNullable()) {
            addImport("java.util.Objects");
            appendLines(members.stream()
                    .filter(member -> !member.getNullable())
                    .filter(member -> !ClassUtil.primitiveToObjectMap.containsKey(member.getType()))
                    .map(Member::getName)
                    .map(name -> String.format("Objects.requireNonNull(%s, \"%s may not be null\");", name, name))
            );
            emptyLine();
        }
    }

    private void appendStringEmptyCheck() {
        if (specification.hasNonEmpty()) {
            appendLines(members.stream()
                    .filter(member -> member.getType().equals("String"))
                    .filter(Member::getNotAcceptEmpty)
                    .map(Member::getName)
                    .map(name -> "if (" + name + ".isEmpty()) throw new IllegalArgumentException(" + quote(name + " may not be empty") + ");")
            );
            emptyLine();
        }
    }

    private void appendConstructorAssignment() {
        appendLines(members.stream()
                .map(Member::getName)
                .map(name -> String.format("this.%s = %s;", name, name))
        );
    }

}
