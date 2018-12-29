package com.triffer.testcontainers.repository.basic;

import java.util.Optional;
import java.util.Set;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.triffer.testcontainers.model.Person;
import com.triffer.testcontainers.repository.PersonRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = PersonRepositoryTest.Initializer.class)
public class PersonRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer postgresContainer = new PostgreSQLContainer().withPassword("test")
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
    private PersonRepository subjectUnderTest;

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/person/findByNameBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/person/findByNameAfter.sql")
    })
    public void findByName() {
        // when
        Set<Person> result = subjectUnderTest.findByName("John");

        // then
        assertEquals(2, result.size());
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/person/findByIdWithMessagesBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/person/findByIdWithMessagesAfter.sql")
    })
    public void findByIdWithMessages() {
        // when
        Optional<Person> result = subjectUnderTest.findById(1L);

        // then
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMessages());
        assertEquals(2, result.get().getMessages().size());
    }

}
