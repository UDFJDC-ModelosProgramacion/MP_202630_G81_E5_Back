package co.edu.udistrital.mdp.pets.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class VaccinationRecordService {

    final PetRepository petRepository;

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public VaccinationRecordEntity createVaccinationRecord(Long petId, VaccinationRecordEntity recordEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create vaccination record for pet with id = {0}", petId);
		PetEntity petEntity = getPetOrThrow(petId);

		if (petEntity.getVaccinationRecord() != null)
			throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_ALREADY_EXISTS);

		if (!validateVaccinationRecord(recordEntity))
			throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_NOT_VALID);

		petEntity.setVaccinationRecord(recordEntity);
		petRepository.save(petEntity);
		log.info("Finished process to create vaccination record for pet with id = {0}", petId);
		return petEntity.getVaccinationRecord();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public VaccinationRecordEntity getVaccinationRecord(Long petId) throws EntityNotFoundException {
		log.info("Starting process to fetch vaccination record of pet with id = {0}", petId);
		PetEntity petEntity = getPetOrThrow(petId);

		if (petEntity.getVaccinationRecord() == null)
			throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

		log.info("Finished process to fetch vaccination record of pet with id = {0}", petId);
		return petEntity.getVaccinationRecord();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public VaccinationRecordEntity updateVaccinationRecord(Long petId, VaccinationRecordEntity record)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update vaccination record of pet with id = {0}", petId);
		PetEntity petEntity = getPetOrThrow(petId);

		if (petEntity.getVaccinationRecord() == null)
			throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

		if (!validateVaccinationRecord(record))
			throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_NOT_VALID);

		record.setId(petEntity.getVaccinationRecord().getId());
		petEntity.setVaccinationRecord(record);
		petRepository.save(petEntity);
		log.info("Finished process to update vaccination record of pet with id = {0}", petId);
		return petEntity.getVaccinationRecord();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public void deleteVaccinationRecord(Long petId) throws EntityNotFoundException {
		log.info("Starting process to delete vaccination record of pet with id = {0}", petId);
		PetEntity petEntity = getPetOrThrow(petId);

		if (petEntity.getVaccinationRecord() == null)
			throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

		petEntity.setVaccinationRecord(null);
		petRepository.save(petEntity);
		log.info("Finished process to delete vaccination record of pet with id = {0}", petId);
	}

	private PetEntity getPetOrThrow(Long petId) throws EntityNotFoundException {
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
		return petOptional.get();
	}

	private boolean validateVaccinationRecord(VaccinationRecordEntity record) {
		return record.getVaccinesApplied() != null && !record.getVaccinesApplied().isEmpty();
	}
}