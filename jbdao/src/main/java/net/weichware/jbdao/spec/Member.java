package net.weichware.jbdao.spec;


import net.weichware.jbdao.util.NameUtil;

public class Member {
    private String name;
    private String type;

    private String databaseName;
    private String csvName;
    private String jsonName;
    private String displayName;

    private boolean primary;
    private Boolean immutable;
    private Boolean nullable;
    private Boolean acceptEmpty;
    private Boolean with;
    private Boolean getter;
    private Boolean toString;
    private Boolean hashEquals;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimary() {
        return primary;
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

    public Boolean generateGetter() {
        if (getter == null) {
            getter = true;
        }
        return getter;
    }

    public Boolean isImmutable() {
        if (immutable == null) {
            immutable = true;
        }
        return immutable;
    }

    public Boolean isNullable() {
        if (nullable == null) {
            nullable = false;
        }
        return nullable;
    }

    public Boolean isNotNullable() {
        return !isNullable();
    }

    public boolean acceptsEmpty() {
        if (acceptEmpty == null) {
            acceptEmpty = isNullable();
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
