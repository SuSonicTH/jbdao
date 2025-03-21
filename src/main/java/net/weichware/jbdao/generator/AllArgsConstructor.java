package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.CodeWriter;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class AllArgsConstructor extends CodeWriter {
    private final List<Member> members;
    private final Specification specification;

    public AllArgsConstructor(Specification specification) {
        super(1);
        this.specification = specification;
        this.members = specification.getMembers();

        if (specification.hasAllArgsConstructor()) {
            generateCode();
        }
    }

    private void generateCode() {
        eol();
        appendLine("public %s(%s) {", specification.getName(), constructorArgumentList());
        if (specification.hasNonNullable()) {
            addImport("java.util.Objects");
            appendLines(objectsNotNull());
            eol();
        }
        if (specification.hasNonEmpty()) {
            appendLines(stringEmpty());
            eol();
        }
        appendLines(constructorAssignment());
        appendLine("}");
    }

    private Stream<String> stringEmpty() {
        return members.stream()
                .filter(member -> member.getType().equals("String"))
                .filter(Member::getNotAcceptEmpty)
                .map(Member::getName)
                .map(name -> "if (" + name + ".isEmpty()) throw new IllegalArgumentException(" + quote(name + " may not be empty") + ");");

    }

    private Stream<String> objectsNotNull() {
        return members.stream()
                .filter(member -> !member.getNullable())
                .map(Member::getName)
                .map(name -> String.format("Objects.requireNonNull(%s, \"%s my not be null\");", name, name));
    }

    private Stream<String> constructorAssignment() {
        return members.stream()
                .map(Member::getName)
                .map(name -> String.format("this.%s = %s;", name, name));
    }

    private String constructorArgumentList() {
        return members.stream()
                .map(member -> member.getType() + " " + member.getName())
                .collect(joining(", "));
    }

}
