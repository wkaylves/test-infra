package com.github.kaylves.test.infra.spring.mvc;

import com.github.kaylves.test.infra.core.matcher.JsonPathMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @BeforeEach
    void assertMockMvcInjected() {
        if (mockMvc == null) {
            throw new IllegalStateException(
                    "MockMvc not injected. Annotate test class with @WebMvcTest or @SpringBootTest + @AutoConfigureMockMvc.");
        }
    }

    protected MvcTestResult performGet(String url) {
        return execute(MockMvcRequestBuilders.get(url));
    }

    protected MvcTestResult performGet(String url, Map<String, String> headers) {
        return execute(applyHeaders(MockMvcRequestBuilders.get(url), headers));
    }

    protected MvcTestResult performGet(String url, Map<String, String> headers, Map<String, String> queryParams) {
        MockHttpServletRequestBuilder builder = applyQueryParams(MockMvcRequestBuilders.get(url), queryParams);
        return execute(applyHeaders(builder, headers));
    }

    protected MvcTestResult performPost(String url, String requestBody) {
        return executeWithBody(MockMvcRequestBuilders.post(url), requestBody);
    }

    protected MvcTestResult performPost(String url, String requestBody, Map<String, String> headers) {
        return executeWithBody(applyHeaders(MockMvcRequestBuilders.post(url), headers), requestBody);
    }

    protected MvcTestResult performPut(String url, String requestBody) {
        return executeWithBody(MockMvcRequestBuilders.put(url), requestBody);
    }

    protected MvcTestResult performPut(String url, String requestBody, Map<String, String> headers) {
        return executeWithBody(applyHeaders(MockMvcRequestBuilders.put(url), headers), requestBody);
    }

    protected MvcTestResult performPatch(String url, String requestBody) {
        return executeWithBody(MockMvcRequestBuilders.patch(url), requestBody);
    }

    protected MvcTestResult performPatch(String url, String requestBody, Map<String, String> headers) {
        return executeWithBody(applyHeaders(MockMvcRequestBuilders.patch(url), headers), requestBody);
    }

    protected MvcTestResult performDelete(String url) {
        return execute(MockMvcRequestBuilders.delete(url));
    }

    protected MvcTestResult performDelete(String url, Map<String, String> headers) {
        return execute(applyHeaders(MockMvcRequestBuilders.delete(url), headers));
    }

    protected MvcTestResult performDeleteWithBody(String url, String requestBody) {
        return executeWithBody(MockMvcRequestBuilders.delete(url), requestBody);
    }

    protected MvcTestResult performDeleteWithBody(String url, String requestBody, Map<String, String> headers) {
        return executeWithBody(applyHeaders(MockMvcRequestBuilders.delete(url), headers), requestBody);
    }

    private MvcTestResult execute(MockHttpServletRequestBuilder builder) {
        try {
            MvcResult result = mockMvc.perform(builder
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8.name()))
                    .andDo(print())
                    .andReturn();
            int status = result.getResponse().getStatus();
            String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            return new MvcTestResult(status, JsonPathMatcher.from(body));
        } catch (Exception e) {
            throw new MvcTestException("MockMvc request failed", e);
        }
    }

    private MvcTestResult executeWithBody(MockHttpServletRequestBuilder builder, String requestBody) {
        try {
            MvcResult result = mockMvc.perform(builder
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8.name())
                            .content(requestBody != null ? requestBody : ""))
                    .andDo(print())
                    .andReturn();
            int status = result.getResponse().getStatus();
            String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            return new MvcTestResult(status, JsonPathMatcher.from(body));
        } catch (Exception e) {
            throw new MvcTestException("MockMvc request failed", e);
        }
    }

    private static MockHttpServletRequestBuilder applyHeaders(MockHttpServletRequestBuilder builder,
                                                              Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(builder::header);
        }
        return builder;
    }

    private static MockHttpServletRequestBuilder applyQueryParams(MockHttpServletRequestBuilder builder,
                                                                  Map<String, String> queryParams) {
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        return builder;
    }
}
