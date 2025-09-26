package net.weichware.jbdao.generator;

import net.weichware.jbdao.spec.Member;
import net.weichware.jbdao.spec.Specification;
import net.weichware.jbdao.writer.Generator;

import java.util.List;
import java.util.stream.Collectors;

public class GsonAdapterGenerator extends Generator {
    private static final String SPACE = "        ";

    public GsonAdapterGenerator(Specification specification) {
        super(specification);

        if (specification.generateJson()) {
            addImport("com.google.gson.Gson");
            List<String> enumList = members.stream()
                    .filter(Member::isEnum)
                    .map(Member::type)
                    .distinct()
                    .collect(Collectors.toList());

            if (enumList.isEmpty()) {
                appendLine("public static final Gson GSON = GsonUtil.GSON;");
            } else {
                appendLine("public static final Gson GSON = GsonUtil.GSON_BUILDER");
                enumList.forEach(enumName ->
                        appendLine(SPACE + ".registerTypeAdapter(%s.class, new %s.GsonAdapter().nullSafe())", enumName, enumName)
                );
                appendLine(SPACE + ".create();");
            }
        }

    }

}
