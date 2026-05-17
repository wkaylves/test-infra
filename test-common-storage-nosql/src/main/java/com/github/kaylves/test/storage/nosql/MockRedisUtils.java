package com.github.kaylves.test.storage.nosql;

import org.mockito.Mockito;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class MockRedisUtils {

    private MockRedisUtils() {
    }

    @SuppressWarnings("unchecked")
    public static RedisTemplate<String, Object> mockRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOps = Mockito.mock(ValueOperations.class);
        HashOperations<String, Object, Object> hashOps = Mockito.mock(HashOperations.class);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOps);
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hashOps);

        Mockito.when(valueOps.get(Mockito.anyString())).thenReturn(null);
        Mockito.doNothing().when(valueOps).set(Mockito.anyString(), Mockito.any());
        Mockito.doNothing().when(valueOps).set(Mockito.anyString(), Mockito.any(),
                Mockito.anyLong(), Mockito.any());

        Mockito.when(redisTemplate.delete(Mockito.anyString())).thenReturn(true);
        Mockito.when(redisTemplate.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisTemplate.expire(Mockito.anyString(), Mockito.anyLong(), Mockito.any())).thenReturn(true);

        return redisTemplate;
    }
}
