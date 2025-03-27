package net.weichware.jbdao.spec;

import com.google.gson.Gson;
import net.weichware.jbdao.util.NameUtil;

import java.util.List;

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
}
