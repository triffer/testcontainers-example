package com.triffer.testcontainers.repository.singleton;

import com.triffer.testcontainers.person.Person;
import com.triffer.testcontainers.person.PersonRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

public class PersonRepositoryTest extends RepositoryTestBase {

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
