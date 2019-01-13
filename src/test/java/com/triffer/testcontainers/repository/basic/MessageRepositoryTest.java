package com.triffer.testcontainers.repository.basic;

import java.util.Set;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.triffer.testcontainers.message.Message;
import com.triffer.testcontainers.message.MessageRepository;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = MessageRepositoryTest.Initializer.class)
class MessageRepositoryTest {

    @Container
    private static PostgreSQLContainer postgresContainer = new PostgreSQLContainer().withPassword("test")
            .withUsername("test");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of("spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.password=" + postgresContainer.getPassword(),
                    "spring.datasource.username=" + postgresContainer.getUsername());
            values.applyTo(configurableApplicationContext);
        }
    }

    @Autowired
    private MessageRepository subjectUnderTest;

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/message/findBySubjectContainingIgnoreCaseBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/message/clean.sql")
    })
    void findBySubjectContainingIgnoreCase() {
        // when
        Set<Message> result = subjectUnderTest.findBySubjectContainingIgnoreCase("spam");

        // then
        assertEquals(2, result.size());
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/message/findByTextContainingIgnoreCaseBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/message/clean.sql")
    })
    void findByTextContainingIgnoreCase() {
        // when
        Set<Message> result = subjectUnderTest.findByTextContainingIgnoreCase("important");

        // then
        assertEquals(1, result.size());
    }

}
