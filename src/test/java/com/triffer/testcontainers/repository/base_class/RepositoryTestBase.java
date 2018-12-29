package com.triffer.testcontainers.repository.base_class;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = RepositoryTestBase.Initializer.class)
public abstract class RepositoryTestBase {

    // We can't use the ClassRule here, because after the first tests the connection pool would be closed
    private static PostgreSQLContainer postgresContainer = new PostgreSQLContainer().withPassword("test")
            .withUsername("test");

    // Starting the container like this it is now shared between all tests that use this class, so parallel DB tests
    // may lead to problems
    static {
        postgresContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of("spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.password=" + postgresContainer.getPassword(),
                    "spring.datasource.username=" + postgresContainer.getUsername());
            values.applyTo(configurableApplicationContext);
        }
    }
}
