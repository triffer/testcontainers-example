package com.triffer.testcontainers.repository.base_class;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import com.triffer.testcontainers.ui.Person;
import com.triffer.testcontainers.ui.PersonRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

class PersonRepositoryTest extends RepositoryTestBase {

    @Autowired
    private PersonRepository subjectUnderTest;

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/person/findByNameBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/person/findByNameAfter.sql")
    })
    void findByName() {
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
    void findByIdWithMessages() {
        // when
        Optional<Person> result = subjectUnderTest.findById(1L);

        // then
        assertTrue(result.isPresent());
        assertNotNull(result.get().getMessages());
        assertEquals(2, result.get().getMessages().size());
    }

}
