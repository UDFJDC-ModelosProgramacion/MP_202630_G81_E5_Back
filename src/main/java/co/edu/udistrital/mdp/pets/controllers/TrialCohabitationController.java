package co.edu.udistrital.mdp.pets.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDTO;
import co.edu.udistrital.mdp.pets.dto.TrialCohabitationDetailDTO;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.TrialCohabitationService;

/**
 * Clase que implementa el recurso DEPENDIENTE y SINGULAR (relacion 1 a 1)
 * "trialCohabitation", anidado bajo "/adoptions/{adoptionId}/trialCohabitation".
 * Sin findAll: cada adoption tiene a lo sumo una trialCohabitation.
 */
@RestController
@RequestMapping("/adoptions")
public class TrialCohabitationController {

	@Autowired
	private TrialCohabitationService trialCohabitationService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping(value = "/{adoptionId}/trialCohabitation")
	@ResponseStatus(code = HttpStatus.OK)
	public TrialCohabitationDetailDTO findOne(@PathVariable("adoptionId") Long adoptionId)
			throws EntityNotFoundException {
		TrialCohabitationEntity trialCohabitationEntity = trialCohabitationService.getTrialCohabitation(adoptionId);
		return modelMapper.map(trialCohabitationEntity, TrialCohabitationDetailDTO.class);
	}

	@PostMapping(value = "/{adoptionId}/trialCohabitation")
	@ResponseStatus(code = HttpStatus.CREATED)
	public TrialCohabitationDTO create(@PathVariable("adoptionId") Long adoptionId,
			@RequestBody TrialCohabitationDTO trialCohabitationDTO)
			throws EntityNotFoundException, IllegalOperationException {
		TrialCohabitationEntity trialCohabitationEntity = trialCohabitationService.createTrialCohabitation(
				adoptionId, modelMapper.map(trialCohabitationDTO, TrialCohabitationEntity.class));
		return modelMapper.map(trialCohabitationEntity, TrialCohabitationDTO.class);
	}

	@PutMapping(value = "/{adoptionId}/trialCohabitation")
	@ResponseStatus(code = HttpStatus.OK)
	public TrialCohabitationDTO update(@PathVariable("adoptionId") Long adoptionId,
			@RequestBody TrialCohabitationDTO trialCohabitationDTO)
			throws EntityNotFoundException, IllegalOperationException {
		TrialCohabitationEntity trialCohabitationEntity = trialCohabitationService.updateTrialCohabitation(
				adoptionId, modelMapper.map(trialCohabitationDTO, TrialCohabitationEntity.class));
		return modelMapper.map(trialCohabitationEntity, TrialCohabitationDTO.class);
	}

	@DeleteMapping(value = "/{adoptionId}/trialCohabitation")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("adoptionId") Long adoptionId) throws EntityNotFoundException {
		trialCohabitationService.deleteTrialCohabitation(adoptionId);
	}
}
