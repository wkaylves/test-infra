package com.github.kaylves.test.infra.spring.mvc;

import com.github.kaylves.test.infra.core.matcher.JsonPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    protected JsonPathMatcher performGetAndMatch(String url) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performPostAndMatch(String url, String requestBody) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(requestBody != null ? requestBody : ""))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performPutAndMatch(String url, String requestBody) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(requestBody != null ? requestBody : ""))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }

    protected JsonPathMatcher performDeleteAndMatch(String url) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name()))
                .andDo(print())
                .andReturn();
        return JsonPathMatcher.from(result.getResponse().getContentAsString(StandardCharsets.UTF_8));
    }
}
