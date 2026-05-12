package de.muenchen.raumreservierung.person;

import de.muenchen.raumreservierung.person.domain.Person;
import de.muenchen.raumreservierung.person.dto.PersonFilterDto;
import de.muenchen.raumreservierung.person.dto.PersonMapper;
import de.muenchen.raumreservierung.person.dto.PersonRequestDto;
import de.muenchen.raumreservierung.person.dto.PersonResponseDto;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    @GetMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDto findById(@PathVariable final UUID personId) {
        return personMapper.toDto(personService.findById(personId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonResponseDto> getPersonsByPageableAndFilter(@ParameterObject final Pageable pageable,
                                                                 @ParameterObject final PersonFilterDto personFilterDto) {
        final Page<Person> personPage = personService.getPersonsByPageableAndFilter(pageable, personFilterDto);

        return personPage.map(personMapper::toDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponseDto createPerson(@RequestBody final PersonRequestDto personDto) {
        return personMapper.toDto(personService.createPerson(personMapper.toEntity(personDto)));
    }

    @PutMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public PersonResponseDto updatePerson(@PathVariable("personId") final UUID personId, @RequestBody final PersonRequestDto personDto) {
        return personMapper.toDto(personService.updatePerson(personId, personMapper.toEntity(personDto)));
    }

    @DeleteMapping("/{personId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable("personId") final UUID personId) {
        personService.deletePerson(personId);
    }
}
