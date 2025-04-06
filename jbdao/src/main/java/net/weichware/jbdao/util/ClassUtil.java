package net.weichware.jbdao.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class ClassUtil {
    public static final HashMap<String, String> primitiveToObjectMap = new HashMap<>();
    public static final HashSet<String> javaBuildIn = new HashSet<>();
    public static final HashMap<String, String> knownClasses = new HashMap<>();
    public static final HashSet<String> numericClasses = new HashSet<>();
    public static final HashSet<String> primitive = new HashSet<>();

    static {
        addPrimitiveToClass(int.class, Integer.class);
        addPrimitiveToClass(long.class, Long.class);
        addPrimitiveToClass(double.class, Double.class);
        addPrimitiveToClass(float.class, Float.class);
        addPrimitiveToClass(boolean.class, Boolean.class);
        addPrimitiveToClass(char.class, Character.class);
        addPrimitiveToClass(byte.class, Byte.class);
        addPrimitiveToClass(void.class, Void.class);
        addPrimitiveToClass(short.class, Short.class);

        javaBuildIn.add("int");
        javaBuildIn.add("long");
        javaBuildIn.add("double");
        javaBuildIn.add("float");
        javaBuildIn.add("boolean");
        javaBuildIn.add("char");
        javaBuildIn.add("byte");
        javaBuildIn.add("void");
        javaBuildIn.add("short");

        addBuildIn(String.class);
        addBuildIn(Integer.class);
        addBuildIn(Long.class);
        addBuildIn(Double.class);
        addBuildIn(Float.class);
        addBuildIn(Boolean.class);
        addBuildIn(Character.class);
        addBuildIn(Byte.class);
        addBuildIn(Void.class);
        addBuildIn(Short.class);

        numericClasses.add("int");
        numericClasses.add("long");
        numericClasses.add("double");
        numericClasses.add("float");
        numericClasses.add("short");
        numericClasses.add("Integer");
        numericClasses.add("Long");
        numericClasses.add("Double");
        numericClasses.add("Float");
        numericClasses.add("Short");

        addKnownClass(LocalDate.class);
        addKnownClass(LocalDateTime.class);
        addKnownClass(Date.class);
    }

    private static void addPrimitiveToClass(Class<?> primitive, Class<?> object) {
        primitiveToObjectMap.put(primitive.getSimpleName(), object.getSimpleName());
    }

    private static void addBuildIn(Class<?> clazz) {
        javaBuildIn.add(clazz.getName());
        javaBuildIn.add(clazz.getSimpleName());
    }

    private static void addKnownClass(Class<?> clazz) {
        knownClasses.put(clazz.getSimpleName(), clazz.getName());
    }

}
