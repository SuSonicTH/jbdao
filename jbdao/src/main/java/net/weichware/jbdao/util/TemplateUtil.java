package net.weichware.jbdao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateUtil {
    public static String getTemplate(String fileName, Map<String, String> values) throws IOException {
        String template = getResourceFileAsString(fileName);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return template;
    }

    public static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = TemplateUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException("Could not load resource " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}
