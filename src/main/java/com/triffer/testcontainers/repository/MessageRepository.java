package com.triffer.testcontainers.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triffer.testcontainers.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Set<Message> findBySubjectContainingIgnoreCase(String subject);

    Set<Message> findByTextContainingIgnoreCase(String text);
}
