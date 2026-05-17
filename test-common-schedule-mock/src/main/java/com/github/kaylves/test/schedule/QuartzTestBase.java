package com.github.kaylves.test.schedule;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "spring.quartz.auto-startup=false"
})
public @interface QuartzTestBase {
}
