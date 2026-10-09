package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

import co.edu.udistrital.mdp.pets.dto.ShelterEventDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterEventService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/shelters")
public class ShelterShelterEventController {

	private final ShelterEventService shelterEventService;

	private final ModelMapper modelMapper;

	@GetMapping(value = "/{shelterId}/shelterEvents")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ShelterEventDTO> findAll(@PathVariable("shelterId") Long shelterId) throws EntityNotFoundException {
		List<ShelterEventEntity> shelterEvents = shelterEventService.getShelterEvents(shelterId);
		return modelMapper.map(shelterEvents, new TypeToken<List<ShelterEventDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{shelterId}/shelterEvents/{shelterEventId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ShelterEventDTO findOne(@PathVariable("shelterId") Long shelterId,
			@PathVariable("shelterEventId") Long shelterEventId)
			throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity shelterEventEntity = shelterEventService.getShelterEvent(shelterId, shelterEventId);
		return modelMapper.map(shelterEventEntity, ShelterEventDTO.class);
	}

	@PostMapping(value = "/{shelterId}/shelterEvents")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ShelterEventDTO create(@PathVariable("shelterId") Long shelterId,
			@RequestBody ShelterEventDTO shelterEventDTO) throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity shelterEventEntity = shelterEventService.createShelterEvent(shelterId,
				modelMapper.map(shelterEventDTO, ShelterEventEntity.class));
		return modelMapper.map(shelterEventEntity, ShelterEventDTO.class);
	}

	@PutMapping(value = "/{shelterId}/shelterEvents/{shelterEventId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ShelterEventDTO update(@PathVariable("shelterId") Long shelterId,
			@PathVariable("shelterEventId") Long shelterEventId, @RequestBody ShelterEventDTO shelterEventDTO)
			throws EntityNotFoundException, IllegalOperationException {
		ShelterEventEntity shelterEventEntity = shelterEventService.updateShelterEvent(shelterId, shelterEventId,
				modelMapper.map(shelterEventDTO, ShelterEventEntity.class));
		return modelMapper.map(shelterEventEntity, ShelterEventDTO.class);
	}

	@DeleteMapping(value = "/{shelterId}/shelterEvents/{shelterEventId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("shelterId") Long shelterId,
			@PathVariable("shelterEventId") Long shelterEventId)
			throws EntityNotFoundException, IllegalOperationException {
		shelterEventService.deleteShelterEvent(shelterId, shelterEventId);
	}
}