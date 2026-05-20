package com.github.kaylves.test.infra.spring.mvc

import com.github.kaylves.test.infra.core.matcher.JsonPathMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

abstract class BaseControllerSpec extends Specification {

    @Autowired
    protected MockMvc mockMvc

    def setup() {
        assert mockMvc != null : "MockMvc not injected. Annotate test class with @WebMvcTest or @SpringBootTest."
    }

    protected MvcTestResult performGet(String url) {
        execute(MockMvcRequestBuilders.get(url))
    }

    protected MvcTestResult performGet(String url, Map<String, String> headers) {
        execute(applyHeaders(MockMvcRequestBuilders.get(url), headers))
    }

    protected MvcTestResult performGet(String url, Map<String, String> headers, Map<String, String> queryParams) {
        def builder = applyQueryParams(MockMvcRequestBuilders.get(url), queryParams)
        execute(applyHeaders(builder, headers))
    }

    protected MvcTestResult performPost(String url, String requestBody) {
        executeWithBody(MockMvcRequestBuilders.post(url), requestBody)
    }

    protected MvcTestResult performPost(String url, String requestBody, Map<String, String> headers) {
        executeWithBody(applyHeaders(MockMvcRequestBuilders.post(url), headers), requestBody)
    }

    protected MvcTestResult performPut(String url, String requestBody) {
        executeWithBody(MockMvcRequestBuilders.put(url), requestBody)
    }

    protected MvcTestResult performPut(String url, String requestBody, Map<String, String> headers) {
        executeWithBody(applyHeaders(MockMvcRequestBuilders.put(url), headers), requestBody)
    }

    protected MvcTestResult performPatch(String url, String requestBody) {
        executeWithBody(MockMvcRequestBuilders.patch(url), requestBody)
    }

    protected MvcTestResult performPatch(String url, String requestBody, Map<String, String> headers) {
        executeWithBody(applyHeaders(MockMvcRequestBuilders.patch(url), headers), requestBody)
    }

    protected MvcTestResult performDelete(String url) {
        execute(MockMvcRequestBuilders.delete(url))
    }

    protected MvcTestResult performDelete(String url, Map<String, String> headers) {
        execute(applyHeaders(MockMvcRequestBuilders.delete(url), headers))
    }

    protected MvcTestResult performDeleteWithBody(String url, String requestBody) {
        executeWithBody(MockMvcRequestBuilders.delete(url), requestBody)
    }

    protected MvcTestResult performDeleteWithBody(String url, String requestBody, Map<String, String> headers) {
        executeWithBody(applyHeaders(MockMvcRequestBuilders.delete(url), headers), requestBody)
    }

    private MvcTestResult execute(builder) {
        try {
            def result = mockMvc.perform(builder
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8"))
                    .andDo(print())
                    .andReturn()
            int status = result.response.status
            String body = result.response.getContentAsString()
            return new MvcTestResult(status, JsonPathMatcher.from(body))
        } catch (Exception e) {
            throw new MvcTestException("MockMvc request failed", e)
        }
    }

    private MvcTestResult executeWithBody(builder, String requestBody) {
        try {
            def result = mockMvc.perform(builder
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(requestBody ?: ""))
                    .andDo(print())
                    .andReturn()
            int status = result.response.status
            String body = result.response.getContentAsString()
            return new MvcTestResult(status, JsonPathMatcher.from(body))
        } catch (Exception e) {
            throw new MvcTestException("MockMvc request failed", e)
        }
    }

    private static applyHeaders(builder, Map<String, String> headers) {
        if (headers) {
            headers.each { k, v -> builder.header(k, v) }
        }
        return builder
    }

    private static applyQueryParams(builder, Map<String, String> queryParams) {
        if (queryParams) {
            queryParams.each { k, v -> builder.queryParam(k, v) }
        }
        return builder
    }
}
