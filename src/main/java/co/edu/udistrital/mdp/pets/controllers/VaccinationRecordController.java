package co.edu.udistrital.mdp.pets.controllers;

import org.modelmapper.ModelMapper;
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

import co.edu.udistrital.mdp.pets.dto.VaccinationRecordDTO;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VaccinationRecordService;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class VaccinationRecordController {

	private final VaccinationRecordService vaccinationRecordService;
	private final ModelMapper modelMapper;

	@GetMapping(value = "/{petId}/vaccinationRecord")
	@ResponseStatus(code = HttpStatus.OK)
	public VaccinationRecordDTO findOne(@PathVariable("petId") Long petId) throws EntityNotFoundException {
		VaccinationRecordEntity entity = vaccinationRecordService.getVaccinationRecord(petId);
		return modelMapper.map(entity, VaccinationRecordDTO.class);
	}

	@PostMapping(value = "/{petId}/vaccinationRecord")
	@ResponseStatus(code = HttpStatus.CREATED)
	public VaccinationRecordDTO create(@PathVariable("petId") Long petId,
			@RequestBody VaccinationRecordDTO vaccinationRecordDTO)
			throws EntityNotFoundException, IllegalOperationException {
		VaccinationRecordEntity entity = vaccinationRecordService.createVaccinationRecord(petId,
				modelMapper.map(vaccinationRecordDTO, VaccinationRecordEntity.class));
		return modelMapper.map(entity, VaccinationRecordDTO.class);
	}

	@PutMapping(value = "/{petId}/vaccinationRecord")
	@ResponseStatus(code = HttpStatus.OK)
	public VaccinationRecordDTO update(@PathVariable("petId") Long petId,
			@RequestBody VaccinationRecordDTO vaccinationRecordDTO)
			throws EntityNotFoundException, IllegalOperationException {
		VaccinationRecordEntity entity = vaccinationRecordService.updateVaccinationRecord(petId,
				modelMapper.map(vaccinationRecordDTO, VaccinationRecordEntity.class));
		return modelMapper.map(entity, VaccinationRecordDTO.class);
	}

	@DeleteMapping(value = "/{petId}/vaccinationRecord")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("petId") Long petId) throws EntityNotFoundException {
		vaccinationRecordService.deleteVaccinationRecord(petId);
	}
}