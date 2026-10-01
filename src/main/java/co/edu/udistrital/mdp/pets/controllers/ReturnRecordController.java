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

import co.edu.udistrital.mdp.pets.dto.ReturnRecordDTO;
import co.edu.udistrital.mdp.pets.dto.ReturnRecordDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ReturnRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReturnRecordService;
import lombok.RequiredArgsConstructor;
/**
 * Clase que implementa el recurso DEPENDIENTE y SINGULAR (relación 1 a 1)
 * "returnRecord", anidado bajo "/adoptions/{adoptionId}/returnRecord". Sin
 * findAll: cada adoption tiene a lo sumo un returnRecord, no una colección.
 */


@RequiredArgsConstructor
@RestController
@RequestMapping("/adoptions")
public class ReturnRecordController {

	private final ReturnRecordService returnRecordService;
	private final ModelMapper modelMapper;

	@GetMapping(value = "/{adoptionId}/returnRecord")
	@ResponseStatus(code = HttpStatus.OK)
	public ReturnRecordDetailDTO findOne(@PathVariable("adoptionId") Long adoptionId) throws EntityNotFoundException {
		ReturnRecordEntity returnRecordEntity = returnRecordService.getReturnRecord(adoptionId);
		return modelMapper.map(returnRecordEntity, ReturnRecordDetailDTO.class);
	}

	@PostMapping(value = "/{adoptionId}/returnRecord")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ReturnRecordDTO create(@PathVariable("adoptionId") Long adoptionId,
			@RequestBody ReturnRecordDTO returnRecordDTO) throws EntityNotFoundException, IllegalOperationException {
		ReturnRecordEntity returnRecordEntity = returnRecordService.createReturnRecord(adoptionId,
				modelMapper.map(returnRecordDTO, ReturnRecordEntity.class));
		return modelMapper.map(returnRecordEntity, ReturnRecordDTO.class);
	}

	@PutMapping(value = "/{adoptionId}/returnRecord")
	@ResponseStatus(code = HttpStatus.OK)
	public ReturnRecordDTO update(@PathVariable("adoptionId") Long adoptionId,
			@RequestBody ReturnRecordDTO returnRecordDTO) throws EntityNotFoundException, IllegalOperationException {
		ReturnRecordEntity returnRecordEntity = returnRecordService.updateReturnRecord(adoptionId,
				modelMapper.map(returnRecordDTO, ReturnRecordEntity.class));
		return modelMapper.map(returnRecordEntity, ReturnRecordDTO.class);
	}

	@DeleteMapping(value = "/{adoptionId}/returnRecord")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("adoptionId") Long adoptionId) throws EntityNotFoundException {
		returnRecordService.deleteReturnRecord(adoptionId);
	}
}