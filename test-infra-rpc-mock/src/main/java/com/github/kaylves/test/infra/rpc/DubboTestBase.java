package com.github.kaylves.test.infra.rpc;

import org.mockito.Mockito;

/**
 * Base test class for Dubbo service testing.
 *
 * <p>Provides convenience methods for creating mock Dubbo service instances.
 * These methods are wrappers around Mockito.mock() for readability and
 * consistency in Dubbo test scenarios.</p>
 *
 * <p>For Spring-managed Dubbo service injection, consider using
 * {@code @MockBean} on the service field in your test class, which will
 * replace the bean in the ApplicationContext automatically:</p>
 *
 * <pre>{@code
 * {@literal @}SpringBootTest
 * class MyDubboTest {
 *     {@literal @}MockBean
 *     private MyDubboService myDubboService;
 *
 *     {@literal @}Test
 *     void testSomething() {
 *         when(myDubboService.doSomething()).thenReturn(result);
 *         // ...
 *     }
 * }
 * }</pre>
 */
public class DubboTestBase {

    protected <T> T mockDubboService(Class<T> serviceClass) {
        return Mockito.mock(serviceClass);
    }

    protected <T> T stubDubboService(Class<T> serviceClass) {
        return Mockito.mock(serviceClass);
    }
}
