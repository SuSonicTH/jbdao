package net.weichware.jbdao.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NameUtil {
    public static String camelToSnake(String camelName) {
        return String.join("_", new ByCaseSplitter(camelName).split());
    }

    public static String camelToSnakeUpperCase(String camelName) {
        return String.join("_", new ByCaseSplitter(camelName).toUpperCase(true).split());
    }

    public static String camelToSnakeLowerCase(String camelName) {
        return String.join("_", new ByCaseSplitter(camelName).toLowerCase(true).split());
    }

    public static String camelToDisplay(String camelName) {
        return new ByCaseSplitter(camelName).split().stream()
                .map(item -> {
                    if (item.toUpperCase().equals(item)) {
                        return item;
                    } else {
                        return item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase();
                    }
                })
                .collect(Collectors.joining(" "));
    }

    public static String firstCharacterUpper(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String firstCharacterLower(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }


    public static class ByCaseSplitter {
        private final String names;
        private final List<String> list = new ArrayList<>();
        private StringBuilder current = new StringBuilder();
        private boolean toLowerCase = false;
        private boolean toUpperCase = false;

        ByCaseSplitter(String names) {
            this.names = names;
        }

        public ByCaseSplitter toLowerCase(boolean toLowerCase) {
            this.toLowerCase = toLowerCase;
            this.toUpperCase = !toLowerCase;
            return this;
        }

        public ByCaseSplitter toUpperCase(boolean toUpperCase) {
            this.toUpperCase = toUpperCase;
            this.toLowerCase = !toUpperCase;
            return this;
        }


        List<String> split() {
            if (names == null || names.isEmpty()) {
                return list;
            }
            append(0);
            boolean lastWasUpperCase = isUpperCase(0);
            for (int index = 1; index < names.length(); index++) {
                if (isUpperCase(index)) {
                    if (lastWasUpperCase) {
                        if (index + 1 == names.length()) {
                            append(index);
                            addCurrentToList();
                        } else if (!isUpperCase(index + 1)) {
                            addCurrentToList();
                            append(index);
                        } else {
                            append(index);
                        }
                    } else {
                        addCurrentToList();
                        append(index);
                    }
                    lastWasUpperCase = true;
                } else {
                    lastWasUpperCase = false;
                    append(index);
                }
            }
            String last = current.toString();
            if (!last.isEmpty()) {
                list.add(last);
            }
            return list;
        }

        private boolean isUpperCase(int index) {
            return Character.isUpperCase(names.charAt(index));
        }

        private void append(int index) {
            if (toLowerCase) {
                current.append(Character.toLowerCase(names.charAt(index)));
            } else if (toUpperCase) {
                current.append(Character.toUpperCase(names.charAt(index)));
            } else {
                current.append(names.charAt(index));
            }
        }

        private void addCurrentToList() {
            list.add(current.toString());
            current = new StringBuilder();
        }
    }
}
