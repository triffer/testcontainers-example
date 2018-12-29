package com.triffer.testcontainers.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triffer.testcontainers.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Set<Person> findByName(String name);

}
