package one.digitalinnovation.personapi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import one.digitalinnovation.personapi.dtos.MessageResponseDTO;
import one.digitalinnovation.personapi.dtos.PersonDTO;
import one.digitalinnovation.personapi.entities.Person;
import one.digitalinnovation.personapi.exceptions.PersonNotFoundException;
import one.digitalinnovation.personapi.mappers.PersonMapper;
import one.digitalinnovation.personapi.repositories.PersonRepository;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PersonService {

	private PersonRepository repository;
	
	private final PersonMapper personMapper = PersonMapper.INSTANCE;
	
	@Transactional
	public MessageResponseDTO create(PersonDTO personDto) {
		Person personToSave = personMapper.toModel(personDto);
		Person personSaved = repository.save(personToSave);
		return responseMessageDTO(personSaved.getId(), "Created person with ID ");
	}

	@Transactional(readOnly = true)
	public List<PersonDTO> listAll() {
		List<Person> allPerson = repository.findAll();
		return allPerson.stream()
				.map(personMapper::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public PersonDTO findById(Long id) throws PersonNotFoundException {
		Person person = verifyIfExists(id);
		
		return personMapper.toDTO(person);
	}

	@Transactional
	public void delete(Long id) throws PersonNotFoundException {
		Person person = verifyIfExists(id);
		repository.delete(person);
	}
	
	public MessageResponseDTO update(Long id, PersonDTO personDto) throws PersonNotFoundException {
		verifyIfExists(id);
		Person personToSave = personMapper.toModel(personDto);
		repository.save(personToSave);
		return responseMessageDTO(id, "Updated person with ID ");
	}
	
	private Person verifyIfExists(Long id) throws PersonNotFoundException {
		return repository.findById(id)
			.orElseThrow(() -> new PersonNotFoundException(id));
	}
	
	private MessageResponseDTO responseMessageDTO(Long id, String message) {
		return MessageResponseDTO
				.builder()
				.message(message + id)
				.build();
	}
	
}
