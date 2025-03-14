package net.weichware.jbdao.spec;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Specification {
    private final String name;
    private final List<Member> members;
    private final boolean json;
    private final boolean csv;
    private final Boolean with;

    public Boolean getWith() {
        return with==null?true:false;
    }

    public static Specification readSpec(String spec) {
        return new Gson().fromJson(spec, Specification.class);
    }
}
