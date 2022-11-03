package com.inmods.routeplanner.repository;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer {
    private static GenericContainer redis;

    static {
        redis = new GenericContainer("redis:6-alpine")
                .withExposedPorts(6379);
        redis.start();
        System.setProperty("spring.redis.host", redis.getContainerIpAddress());
        System.setProperty("spring.redis.port", redis.getFirstMappedPort() + "");
    }
}
