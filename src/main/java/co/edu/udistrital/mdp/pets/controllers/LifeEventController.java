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

import co.edu.udistrital.mdp.pets.dto.LifeEventDetailDTO;
import co.edu.udistrital.mdp.pets.entities.LifeEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.LifeEventService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class LifeEventController {

	private final LifeEventService lifeEventService;
	private final ModelMapper modelMapper;

	@PostMapping(value = "/{petId}/lifeEvents")
	@ResponseStatus(code = HttpStatus.CREATED)
	public LifeEventDetailDTO create(@PathVariable("petId") Long petId,
			@RequestBody LifeEventDetailDTO lifeEventDTO)
			throws EntityNotFoundException, IllegalOperationException {
		LifeEventEntity entity = lifeEventService.createLifeEvent(petId,
				modelMapper.map(lifeEventDTO, LifeEventEntity.class));
		return modelMapper.map(entity, LifeEventDetailDTO.class);
	}

	@GetMapping(value = "/{petId}/lifeEvents")
	@ResponseStatus(code = HttpStatus.OK)
	public List<LifeEventDetailDTO> findAll(@PathVariable("petId") Long petId) throws EntityNotFoundException {
		List<LifeEventEntity> lifeEvents = lifeEventService.getLifeEvents(petId);
		return modelMapper.map(lifeEvents, new TypeToken<List<LifeEventDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{petId}/lifeEvents/{lifeEventId}")
	@ResponseStatus(code = HttpStatus.OK)
	public LifeEventDetailDTO findOne(@PathVariable("petId") Long petId,
			@PathVariable("lifeEventId") Long lifeEventId)
			throws EntityNotFoundException, IllegalOperationException {
		LifeEventEntity entity = lifeEventService.getLifeEvent(petId, lifeEventId);
		return modelMapper.map(entity, LifeEventDetailDTO.class);
	}

	@PutMapping(value = "/{petId}/lifeEvents/{lifeEventId}")
	@ResponseStatus(code = HttpStatus.OK)
	public LifeEventDetailDTO update(@PathVariable("petId") Long petId,
			@PathVariable("lifeEventId") Long lifeEventId,
			@RequestBody LifeEventDetailDTO lifeEventDTO)
			throws EntityNotFoundException, IllegalOperationException {
		LifeEventEntity entity = lifeEventService.updateLifeEvent(petId, lifeEventId,
				modelMapper.map(lifeEventDTO, LifeEventEntity.class));
		return modelMapper.map(entity, LifeEventDetailDTO.class);
	}

	@DeleteMapping(value = "/{petId}/lifeEvents/{lifeEventId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("petId") Long petId, @PathVariable("lifeEventId") Long lifeEventId)
			throws EntityNotFoundException, IllegalOperationException {
		lifeEventService.deleteLifeEvent(petId, lifeEventId);
	}
}
