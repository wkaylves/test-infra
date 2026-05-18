package com.github.kaylves.test.infra.spring.mvc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest(classes = TestApplication)
class BaseIntegrationSpecTest extends BaseIntegrationSpec {

    @Autowired
    private ApplicationContext applicationContext

    def "Spring context should be loaded"() {
        expect:
        applicationContext != null
    }

    def "TestController should be registered as bean"() {
        expect:
        applicationContext.containsBean("testController")
    }
}
