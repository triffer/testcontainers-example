package com.triffer.testcontainers.repository.singleton;

import com.triffer.testcontainers.message.Message;
import com.triffer.testcontainers.message.MessageRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

public class MessageRepositoryTest extends RepositoryTestBase {

    @Autowired
    private MessageRepository subjectUnderTest;

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/message/findBySubjectContainingIgnoreCaseBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/message/clean.sql")
    })
    public void findBySubjectContainingIgnoreCase() {
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
    public void findByTextContainingIgnoreCase() {
        // when
        Set<Message> result = subjectUnderTest.findByTextContainingIgnoreCase("important");

        // then
        assertEquals(1, result.size());
    }

}
