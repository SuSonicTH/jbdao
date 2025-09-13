package net.weichware.jbdao.spec;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.weichware.jbdao.util.NameUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Specification {
    private String packagePath;
    private String name;

    private List<Member> members;

    private String databaseName;
    private String displayName;
    private String csvName;
    private String jsonName;

    private boolean allArgsConstructor;
    private Boolean noArgsConstructor;
    private Boolean nonNullConstructor;

    private boolean with;
    private boolean database;
    private boolean json;
    private boolean csv;
    private boolean builder;

    private Boolean toString;
    private Boolean hashEquals;
    @SerializedName("abstract")

    private boolean generateAbstract;
    private String className;
    private String returnThisType;
    private String returnThis;
    private String constructorVisibility;

    public static Specification readSpec(String spec) {
        return new Gson().fromJson(spec, Specification.class);
    }

    public String getPackagePath() {
        return packagePath;
    }

    public String getName() {
        return name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public boolean hasNonNullConstructor() {
        if (nonNullConstructor == null) {
            nonNullConstructor = true;
        }
        return nonNullConstructor;
    }

    public boolean hasNoArgsConstructor() {
        if (noArgsConstructor == null) {
            noArgsConstructor = generateJson();
        }
        return noArgsConstructor;
    }

    public boolean hasAllArgsConstructor() {
        return allArgsConstructor;
    }

    public String getDatabaseName() {
        if (databaseName == null) {
            databaseName = NameUtil.camelToSnakeUpperCase(name);
        }
        return databaseName;
    }

    public String getDisplayName() {
        if (displayName == null) {
            displayName = NameUtil.camelToDisplay(name);
        }
        return displayName;
    }

    public String getCsvName() {
        if (csvName == null) {
            csvName = getDatabaseName();
        }
        return csvName;
    }

    public String getJsonName() {
        if (jsonName == null) {
            jsonName = name;
        }
        return jsonName;
    }

    public boolean hasNonNullable() {
        return members.stream().anyMatch(member -> !member.isNullable());
    }

    public boolean hasNonEmpty() {
        return members.stream()
                .filter(member -> member.getType().equals("String"))
                .anyMatch(Member::nonEmpty);
    }

    public boolean generateWith() {
        return with;
    }

    public boolean generateDatabase() {
        return database;
    }

    public Boolean generateToString() {
        if (toString == null) {
            toString = true;
        }
        return toString;
    }

    public Boolean generateHashEquals() {
        if (hashEquals == null) {
            hashEquals = true;
        }
        return hashEquals;
    }

    public Boolean generateAbstract() {
        return generateAbstract;
    }

    public boolean generateCsv() {
        return csv;
    }

    public boolean generateJson() {
        return json;
    }

    public Optional<Member> getPrimary() {
        List<Member> primaries = members.stream().filter(Member::isPrimary).collect(Collectors.toList());
        if (primaries.isEmpty()) {
            return Optional.empty();
        } else if (primaries.size() == 1) {
            return Optional.of(primaries.get(0));
        } else {
            throw new SpecificationException("More then one primary columns are not supported, members in " + getName() + " with primary flag :" + members.stream().map(Member::getName).collect(Collectors.joining(",")));
        }
    }

    public boolean hasBuilder() {
        return builder;
    }

    public boolean needsValidation() {
        return members.stream().anyMatch(member -> !member.isNullable() ||
                member.nonEmpty() ||
                !member.acceptsEmpty() ||
                member.getMin() != null ||
                member.getMax() != null ||
                member.getPattern() != null
        );
    }

    public boolean hasPatterns() {
        return members.stream().anyMatch(member -> member.getPattern() != null);
    }

    public boolean hasMinMax() {
        return members.stream().anyMatch(Member::hasMinMax);
    }

    public boolean hasCsv() {
        return csv;
    }

    public String className() {
        if (className == null) {
            className = generateAbstract ? "Abstract" + getName() : getName();
        }
        return className;
    }

    public String returnThisType() {
        if (returnThisType == null) {
            returnThisType = generateAbstract ? "T" : getName();
        }
        return returnThisType;
    }

    public String returnThis() {
        if (returnThis == null) {
            returnThis = String.format("return %sthis;", generateAbstract ? "(T) " : "");
        }
        return returnThis;
    }

    public String constructorVisibility() {
        if (constructorVisibility == null) {
            constructorVisibility = generateAbstract ? "protected" : "public";
        }
        return constructorVisibility;
    }
}
