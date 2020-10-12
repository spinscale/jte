package gg.jte.internal;

import gg.jte.Content;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused") // by template code
public final class TemplateUtils {
    public static Map<String, Object> toMap(Object ... pairs) {
        if (pairs.length == 0) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>(pairs.length / 2);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String)pairs[i], pairs[i + 1]);
        }
        return map;
    }

    public static boolean isAttributeRendered(boolean value) {
        return value;
    }

    public static boolean isAttributeRendered(byte value) {
        return true;
    }

    public static boolean isAttributeRendered(short value) {
        return true;
    }

    public static boolean isAttributeRendered(int value) {
        return true;
    }

    public static boolean isAttributeRendered(long value) {
        return true;
    }

    public static boolean isAttributeRendered(float value) {
        return true;
    }

    public static boolean isAttributeRendered(double value) {
        return true;
    }

    public static boolean isAttributeRendered(String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isAttributeRendered(Content value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isAttributeRendered(Object value) {
        return value != null && value != Boolean.FALSE;
    }
}
