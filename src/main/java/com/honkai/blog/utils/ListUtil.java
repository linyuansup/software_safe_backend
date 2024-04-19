
package com.honkai.blog.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ListUtil {
    public <T, R> List<R> convert(List<T> list, Class<R> clazz) {
        List<R> result = new ArrayList<>();
        for (T t : list) {
            R r = null;
            try {
                r = clazz.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(t, r);
                result.add(r);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
