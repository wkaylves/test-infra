package com.github.kaylves.test.junit5;

import com.github.kaylves.test.core.matcher.JsonPathMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public abstract class BaseControllerTestBase {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    protected MockMvc mockMvc;

    @BeforeEach
    void initMockMvc() {
        setupMockMvc();
    }

    protected void setupMockMvc() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
    }

    protected JsonPathMatcher performGetAndMatch(String url) throws Exception {
        ensureMockMvc();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performPostAndMatch(String url, String requestBody) throws Exception {
        ensureMockMvc();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(requestBody != null ? requestBody : ""))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performPutAndMatch(String url, String requestBody) throws Exception {
        ensureMockMvc();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(requestBody != null ? requestBody : ""))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performDeleteAndMatch(String url) throws Exception {
        ensureMockMvc();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    private void ensureMockMvc() {
        if (this.mockMvc == null) {
            setupMockMvc();
        }
    }
}
