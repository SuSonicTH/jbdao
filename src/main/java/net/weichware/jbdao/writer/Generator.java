package net.weichware.jbdao.writer;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;

import java.util.List;

public class Generator extends CodeWriter {
    protected final Specification specification;
    protected final List<Member> members;

    protected Generator(Specification specification) {
        super(1);

        this.specification = specification;
        this.members = specification.getMembers();
    }
}
