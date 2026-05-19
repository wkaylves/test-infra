package com.github.kaylves.test.infra.rpc;

import org.mockito.Mockito;

/**
 * Base test class for gRPC stub testing.
 *
 * <p>Provides convenience methods for creating mock gRPC stub instances.
 * These methods are wrappers around Mockito.mock() for readability and
 * consistency in gRPC test scenarios.</p>
 *
 * <p>For Spring-managed gRPC stub injection, consider using
 * {@code @MockBean} on the stub field in your test class, which will
 * replace the bean in the ApplicationContext automatically:</p>
 *
 * <pre>{@code
 * {@literal @}SpringBootTest
 * class MyGrpcTest {
 *     {@literal @}MockBean
 *     private MyServiceGrpc.MyServiceBlockingStub myServiceStub;
 *
 *     {@literal @}Test
 *     void testSomething() {
 *         when(myServiceStub.doSomething(any())).thenReturn(response);
 *         // ...
 *     }
 * }
 * }</pre>
 */
public class GrpcTestBase {

    protected <T> T mockGrpcStub(Class<T> stubClass) {
        return Mockito.mock(stubClass);
    }
}
