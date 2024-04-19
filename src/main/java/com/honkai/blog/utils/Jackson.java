
package com.honkai.blog.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;


public class Jackson {
    abstract public static class BaseType<T> extends TypeReference<T> {
    }

    public static final ObjectMapper DEFAULT_OBJECT_MAPPER = initObjectMapper();

    private static ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(getJavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    private static JavaTimeModule getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        return javaTimeModule;
    }

    public static String toJsonString(Object value) {
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException err) {
            throw new IllegalArgumentException("Failed to convert the object" + value.toString()
                    + "to json string", err);
        }
    }

    public static byte[] toByteArray(Object value) {
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsBytes(value);
        } catch (JsonProcessingException err) {
            throw new IllegalArgumentException("Failed to convert the object to json string", err);
        }
    }

    public static Map<String, Object> parse(String text) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(text, new TypeReference<Map<String, Object>>() {});
        } catch (IOException err) {
            throw new IllegalArgumentException("Error parsing: '" + text
                    + "' to map with error", err);
        }
    }

    public static <K, V> Map<K, V> parse(String text, Class<K> keyType, Class<V> valueType) {
        return parse(text, Map.class, keyType, valueType);
    }

    public static <T extends Map<?, ?>, K, V> Map<K, V> parse(String text, Class<T> mapType,
                                                        Class<K> keyType, Class<V> valueType) {
        try {
            MapType javaType = DEFAULT_OBJECT_MAPPER.getTypeFactory().constructMapType(mapType,
                    keyType, valueType);
            return DEFAULT_OBJECT_MAPPER.readValue(text, javaType);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error parsing map text: '" + text
                    + "' to map with error", err);
        }
    }

    public static <T> T parse(String text, TypeReference<T> valueTypeRef) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(text, valueTypeRef);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error parsing object text'" + text
                    + "' with error", err);
        }
    }

    public static <T> T parse(String text, Class<T> targetType) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(text, targetType);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error parsing '" + text
                    + "' with error", err);
        }
    }

    public static <T> List<T> parseList(String text, Class<T> elementType) {
        try {
            JavaType listType = DEFAULT_OBJECT_MAPPER.getTypeFactory()
                    .constructParametricType(ArrayList.class, elementType);
            return DEFAULT_OBJECT_MAPPER.readValue(text, listType);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error parsing '" + text
                    + "' to list with error", err);
        }
    }

    public static String convertObj(Object obj) {
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error converting object '" + obj
                    + "' to Json text with err ", err);
        }
    }

    public static <T> T convertObj(Object obj, Class<T> targetType) {
        return DEFAULT_OBJECT_MAPPER.convertValue(obj, targetType);
    }

    public static String getFieldAsText(Object bean, String field) {
        try {
            JsonNode rootNode = DEFAULT_OBJECT_MAPPER.readTree(convertObj(bean));
            Queue<Map.Entry<String, JsonNode>> queue = new LinkedList<>();
            rootNode.fields().forEachRemaining(queue::offer);
            Map.Entry<String, JsonNode> entry;
            while ((entry = queue.poll()) != null) {
                if (field.equals(entry.getKey()) && !entry.getValue().isContainerNode()) {
                    return entry.getValue().asText();
                }
                if (entry.getValue().isContainerNode()) {
                    entry.getValue().fields().forEachRemaining(queue::offer);
                }
            }
            return null;
        } catch (Exception err) {
            throw new IllegalArgumentException("Failed to get field with error ", err);
        }
    }

    public static <K, V> Map<K, V> toMap(Object bean, Class<K> keyType, Class<V> valueType) {
        try {
            String str = DEFAULT_OBJECT_MAPPER.writeValueAsString(bean);
            return parse(str, Map.class, keyType, valueType);
        } catch (IOException err) {
            throw new IllegalArgumentException("Error converting object '" + bean
                    + "' to map with err ", err);

        }
    }

    public static <K, V> List<Map<K, V>> toListMap(String value) {
        try {
            CollectionType listType = DEFAULT_OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Map.class);
            return DEFAULT_OBJECT_MAPPER.readValue(value, listType);
        } catch (Exception err) {
            throw new IllegalArgumentException("Failed toListMap ", err);
        }
    }
}
