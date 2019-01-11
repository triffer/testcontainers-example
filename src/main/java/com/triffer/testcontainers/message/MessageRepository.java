package com.triffer.testcontainers.message;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Set<Message> findBySubjectContainingIgnoreCase(String subject);

    Set<Message> findByTextContainingIgnoreCase(String text);
}
