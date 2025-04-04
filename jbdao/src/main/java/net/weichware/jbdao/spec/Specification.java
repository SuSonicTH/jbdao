package net.weichware.jbdao.spec;

import com.google.gson.Gson;
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

    private boolean database;
    private boolean json;
    private boolean csv;
    private boolean with;
    private Boolean allArgsConstructor;
    private boolean noArgsConstructor;
    private Boolean toString;
    private Boolean hashEquals;

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

    public boolean hasAllArgsConstructor() {
        if (allArgsConstructor == null) {
            allArgsConstructor = true;
        }
        return allArgsConstructor;
    }

    public boolean hasNoArgsConstructor() {
        return noArgsConstructor;
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
        return members.stream().anyMatch(member -> !member.getNullable());
    }

    public boolean hasNonEmpty() {
        return members.stream()
                .filter(member -> member.getType().equals("String"))
                .anyMatch(Member::getNotAcceptEmpty);
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
}
