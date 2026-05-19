package com.github.kaylves.test.infra.http.openfeign;

import com.github.kaylves.test.infra.http.feign.FeignAnnotationUtils;
import com.github.kaylves.test.infra.http.feign.FeignClientContractStrategy;
import com.github.kaylves.test.infra.http.feign.FeignMethodMeta;
import com.github.kaylves.test.infra.http.feign.FeignParamIndexes;
import feign.Feign;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public final class SpringCloudOpenFeignContractStrategy implements FeignClientContractStrategy {

    private static final String FEIGN_CLIENT = "org.springframework.cloud.openfeign.FeignClient";
    private static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String PATH_VARIABLE = "org.springframework.web.bind.annotation.PathVariable";
    private static final String REQUEST_PARAM = "org.springframework.web.bind.annotation.RequestParam";

    private static final String[][] MVC_MAPPINGS = {
            {"org.springframework.web.bind.annotation.GetMapping", "GET"},
            {"org.springframework.web.bind.annotation.PostMapping", "POST"},
            {"org.springframework.web.bind.annotation.PutMapping", "PUT"},
            {"org.springframework.web.bind.annotation.DeleteMapping", "DELETE"},
            {"org.springframework.web.bind.annotation.PatchMapping", "PATCH"},
    };

    @Override
    public String name() {
        return "spring-cloud-openfeign";
    }

    @Override
    public FeignMethodMeta parse(Class<?> clientClass, Method method) {
        String classPath = resolveClassPath(clientClass);
        for (String[] mapping : MVC_MAPPINGS) {
            Annotation annotation = FeignAnnotationUtils.findAnnotation(method, mapping[0]);
            if (annotation == null) {
                continue;
            }
            String path = FeignAnnotationUtils.firstAnnotationValue(annotation, "value", "path");
            if (path == null) {
                throw new IllegalArgumentException("Spring mapping path is required for " + method.toGenericString() + ".");
            }
            FeignParamIndexes indexes = FeignAnnotationUtils.parseParamAnnotations(method, PATH_VARIABLE, REQUEST_PARAM);
            return new FeignMethodMeta(method, mapping[1], FeignAnnotationUtils.joinPath(classPath, path),
                    indexes.pathVariables(), indexes.queryParams());
        }

        Annotation requestMapping = FeignAnnotationUtils.findAnnotation(method, REQUEST_MAPPING);
        if (requestMapping != null) {
            String path = FeignAnnotationUtils.firstAnnotationValue(requestMapping, "value", "path");
            String httpMethod = FeignAnnotationUtils.firstEnumArrayName(requestMapping, "method");
            if (path == null || httpMethod == null) {
                throw new IllegalArgumentException("@RequestMapping requires path and method for " + method.toGenericString() + ".");
            }
            FeignParamIndexes indexes = FeignAnnotationUtils.parseParamAnnotations(method, PATH_VARIABLE, REQUEST_PARAM);
            return new FeignMethodMeta(method, httpMethod, FeignAnnotationUtils.joinPath(classPath, path),
                    indexes.pathVariables(), indexes.queryParams());
        }
        return null;
    }

    @Override
    public void configure(Feign.Builder builder) {
        try {
            Class<?> contractClass = Class.forName("org.springframework.cloud.openfeign.support.SpringMvcContract");
            builder.contract((feign.Contract) contractClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new IllegalStateException("Spring Cloud OpenFeign annotations require SpringMvcContract on the classpath.", e);
        }
    }

    private String resolveClassPath(Class<?> type) {
        List<String> parts = FeignAnnotationUtils.newPathParts();
        Annotation feignClient = FeignAnnotationUtils.findAnnotation(type, FEIGN_CLIENT);
        if (feignClient != null) {
            FeignAnnotationUtils.addIfPresent(parts, FeignAnnotationUtils.firstAnnotationValue(feignClient, "path"));
            FeignAnnotationUtils.addIfPresent(parts, FeignAnnotationUtils.firstAnnotationValue(feignClient, "url"));
        }
        Annotation requestMapping = FeignAnnotationUtils.findAnnotation(type, REQUEST_MAPPING);
        if (requestMapping != null) {
            FeignAnnotationUtils.addIfPresent(parts, FeignAnnotationUtils.firstAnnotationValue(requestMapping, "value", "path"));
        }
        if (parts.isEmpty()) {
            return "";
        }
        return FeignAnnotationUtils.joinPath(parts.toArray(new String[0]));
    }
}
