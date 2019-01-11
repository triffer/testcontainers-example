package com.triffer.testcontainers.ui;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Set<Person> findByName(String name);

}
