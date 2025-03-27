package net.weichware.jbdao.spec;


import net.weichware.jbdao.util.NameUtil;

public class Member {
    private String name;
    private String type;
    private String databaseName;
    private String csvName;
    private String jsonName;
    private String displayName;
    private Boolean immutable;
    private Boolean nullable;
    private Boolean acceptEmpty;
    private Boolean with;
    private Boolean getter;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Boolean generateGetter() {
        if (getter == null) {
            getter = true;
        }
        return getter;
    }

    public Boolean getImmutable() {
        if (immutable == null) {
            immutable = true;
        }
        return immutable;
    }

    public Boolean getNullable() {
        if (nullable == null) {
            nullable = false;
        }
        return nullable;
    }

    public boolean getNotAcceptEmpty() {
        if (acceptEmpty == null) {
            acceptEmpty = false;
        }
        return !acceptEmpty;
    }

    public boolean generateWith() {
        if (with == null) {
            with = true;
        }
        return with;
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
}
