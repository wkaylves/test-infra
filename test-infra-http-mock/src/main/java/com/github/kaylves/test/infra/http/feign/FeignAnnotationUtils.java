package com.github.kaylves.test.infra.http.feign;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FeignAnnotationUtils {

    private FeignAnnotationUtils() {
    }

    public static Annotation findAnnotation(Method method, String annotationClassName) {
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationClassName)) {
                return annotation;
            }
        }
        return null;
    }

    public static Annotation findAnnotation(Class<?> type, String annotationClassName) {
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation.annotationType().getName().equals(annotationClassName)) {
                return annotation;
            }
        }
        return null;
    }

    public static String firstAnnotationValue(Annotation annotation, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Object value = annotation.annotationType().getMethod(methodName).invoke(annotation);
                String extracted = firstString(value);
                if (extracted != null) {
                    return extracted;
                }
            } catch (NoSuchMethodException ignored) {
                // Try the next common attribute name.
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to read annotation value from " + annotation.annotationType().getName() + ".", e);
            }
        }
        return null;
    }

    public static String firstEnumArrayName(Annotation annotation, String methodName) {
        try {
            Object value = annotation.annotationType().getMethod(methodName).invoke(annotation);
            if (value instanceof Object[]) {
                Object[] values = (Object[]) value;
                return values.length > 0 ? String.valueOf(values[0]) : null;
            }
            return value == null ? null : String.valueOf(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read annotation value from " + annotation.annotationType().getName() + ".", e);
        }
    }

    public static FeignParamIndexes parseParamAnnotations(Method method,
                                                          String pathAnnotationName,
                                                          String queryAnnotationName) {
        Map<Integer, String> pathVariables = new LinkedHashMap<>();
        Map<Integer, String> queryParams = new LinkedHashMap<>();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                String annotationName = annotation.annotationType().getName();
                if (annotationName.equals(pathAnnotationName)) {
                    String name = firstAnnotationValue(annotation, "value", "name");
                    if (name == null) {
                        throw new IllegalArgumentException("Path variable name is required for parameter " + i + " of " + method.toGenericString() + ".");
                    }
                    pathVariables.put(i, name);
                    break;
                }
                if (queryAnnotationName != null && annotationName.equals(queryAnnotationName)) {
                    String name = firstAnnotationValue(annotation, "value", "name");
                    if (name == null) {
                        throw new IllegalArgumentException("Request parameter name is required for parameter " + i + " of " + method.toGenericString() + ".");
                    }
                    queryParams.put(i, name);
                    break;
                }
            }
        }
        return new FeignParamIndexes(pathVariables, queryParams);
    }

    public static String joinPath(String... paths) {
        StringBuilder result = new StringBuilder();
        for (String path : paths) {
            if (path == null || path.trim().isEmpty()) {
                continue;
            }
            String trimmed = path.trim();
            if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                continue;
            }
            if (result.length() > 0 && result.charAt(result.length() - 1) == '/') {
                result.setLength(result.length() - 1);
            }
            if (!trimmed.startsWith("/")) {
                result.append('/');
            }
            result.append(trimmed);
        }
        return result.length() == 0 ? "/" : result.toString();
    }

    public static void addIfPresent(List<String> parts, String value) {
        if (value != null && !value.startsWith("http://") && !value.startsWith("https://")) {
            parts.add(value);
        }
    }

    public static List<String> newPathParts() {
        return new ArrayList<>();
    }

    private static String firstString(Object value) {
        if (value instanceof String) {
            String string = ((String) value).trim();
            return string.isEmpty() ? null : string;
        }
        if (value instanceof String[]) {
            String[] values = (String[]) value;
            if (values.length == 0) {
                return null;
            }
            String string = values[0].trim();
            return string.isEmpty() ? null : string;
        }
        return null;
    }
}
