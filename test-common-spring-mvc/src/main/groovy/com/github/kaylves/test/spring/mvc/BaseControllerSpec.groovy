package com.github.kaylves.test.spring.mvc

import com.github.kaylves.test.core.matcher.JsonPathMatcher
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

    protected JsonPathMatcher performGetAndMatch(String url) {
        def result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andReturn()
        return JsonPathMatcher.from(result.response.getContentAsString())
    }

    protected JsonPathMatcher performPostAndMatch(String url, String requestBody) {
        def result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestBody ?: ""))
                .andDo(print())
                .andReturn()
        return JsonPathMatcher.from(result.response.getContentAsString())
    }

    protected JsonPathMatcher performPutAndMatch(String url, String requestBody) {
        def result = mockMvc.perform(MockMvcRequestBuilders.put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestBody ?: ""))
                .andDo(print())
                .andReturn()
        return JsonPathMatcher.from(result.response.getContentAsString())
    }

    protected JsonPathMatcher performDeleteAndMatch(String url) {
        def result = mockMvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andReturn()
        return JsonPathMatcher.from(result.response.getContentAsString())
    }
}
