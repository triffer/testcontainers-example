package com.triffer.testcontainers.repository.base_class;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = RepositoryTestBase.Initializer.class)
abstract class RepositoryTestBase {

    private static PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer().withPassword("test")
            .withUsername("test");

    // The container will be stopped when the JVM is shut down
    static {
        POSTGRES_CONTAINER.start();
    }

    @ContextConfiguration
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of("spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                    "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword(),
                    "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername());
            values.applyTo(configurableApplicationContext);
        }
    }
}
