package com.triffer.testcontainers.person;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private PersonRepository personRepository;

    public PersonController(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("persons", personRepository.findAll());
        return "persons";
    }
}
