package dev.yong.wheel.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author coderyong
 */
public class MapUtils {

    public static Object mapToObject(Map map, Class<?> clazz) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = clazz.newInstance();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }

            field.setAccessible(true);
            Object o = map.get(field.getName());
            if (o instanceof Collection) {
                Collection collection = (Collection) o;
                Iterator iterator = collection.iterator();
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                Type type = genericType.getActualTypeArguments()[0];
                List list = new ArrayList();
                while (iterator.hasNext()) {
                    list.add(mapToObject((Map) iterator.next(), (Class<?>) type));
                }
                collection.clear();
                collection.addAll(list);
                field.set(obj, collection);
            } else if (o instanceof Map) {
                field.set(obj, mapToObject((Map) o, field.getType()));
            } else {
                field.set(obj, o);
            }
        }
        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

}
