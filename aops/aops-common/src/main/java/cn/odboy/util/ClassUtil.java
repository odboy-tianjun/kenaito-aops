package cn.odboy.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 类 相关
 */
public final class ClassUtil {
    @SuppressWarnings({"unchecked", "all"})
    public static List<Field> getAllFields(Class clazz, List<Field> fields) {
        if (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            getAllFields(clazz.getSuperclass(), fields);
        }
        return fields;
    }
}
