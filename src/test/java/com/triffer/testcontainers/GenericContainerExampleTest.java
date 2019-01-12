package com.triffer.testcontainers;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.apache.http.HttpResponse;
import org.testcontainers.shaded.org.apache.http.client.methods.HttpGet;
import org.testcontainers.shaded.org.apache.http.client.methods.HttpUriRequest;
import org.testcontainers.shaded.org.apache.http.impl.client.HttpClientBuilder;

public class GenericContainerExampleTest {

    @Rule
    public GenericContainer container = new GenericContainer("rabbitmq:3.6.9-management-alpine")
            .withEnv("discovery.type", "single-node").withExposedPorts(15672)
            .waitingFor(Wait.forLogMessage(".*Server startup complete.*", 1));

    @Test
    public void verifyRabbitMqApiIsAvailable() throws IOException {
        // given
        String containerIpAndPort = container.getContainerIpAddress() + ":" + container.getMappedPort(15672);
        HttpUriRequest request = new HttpGet("http://" + containerIpAndPort + "/api");

        // when
        HttpResponse response = HttpClientBuilder.create().build().execute(request);

        // then
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
    }
}
