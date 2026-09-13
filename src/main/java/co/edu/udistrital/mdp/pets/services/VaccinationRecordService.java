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
import co.edu.udistrital.mdp.pets.repositories.VaccinationRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class VaccinationRecordService {

    final VaccinationRecordRepository vaccinationRecordRepository;
    final PetRepository petRepository;

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public VaccinationRecordEntity createVaccinationRecord(Long petId, VaccinationRecordEntity vaccinationRecordEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting the process of creating a vaccination record for pet with id = ", petId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        PetEntity petEntity = petOptional.get();
        if (petEntity.getVaccinationRecord() != null)
            throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_ALREADY_EXISTS);

        if (!validateVaccinationRecord(vaccinationRecordEntity))
            throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_NOT_VALID);

        VaccinationRecordEntity savedRecord = vaccinationRecordRepository.save(vaccinationRecordEntity);
        petEntity.setVaccinationRecord(savedRecord);
        petRepository.save(petEntity);

        log.info("Finished process to create a vaccination record for pet with id = ", petId);
        return savedRecord;
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public VaccinationRecordEntity getVaccinationRecord(Long petId) throws EntityNotFoundException {
        log.info("Starting process to fetch vaccination record of pet with id = ", petId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        VaccinationRecordEntity vaccinationRecord = petOptional.get().getVaccinationRecord();
        if (vaccinationRecord == null)
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

        log.info("Finished process to fetch vaccination record of pet with id = ", petId);
        return vaccinationRecord;
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public VaccinationRecordEntity updateVaccinationRecord(Long petId, VaccinationRecordEntity vaccinationRecordEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update vaccination record of pet with id = ", petId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        VaccinationRecordEntity existingRecord = petOptional.get().getVaccinationRecord();
        if (existingRecord == null)
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

        if (!validateVaccinationRecord(vaccinationRecordEntity))
            throw new IllegalOperationException(ErrorMessage.VACCINATION_RECORD_NOT_VALID);

        vaccinationRecordEntity.setId(existingRecord.getId());
        log.info("Finished process to update vaccination record of pet with id = ", petId);
        return vaccinationRecordRepository.save(vaccinationRecordEntity);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteVaccinationRecord(Long petId) throws EntityNotFoundException {
        log.info("Starting process to delete vaccination record of pet with id = ", petId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        PetEntity petEntity = petOptional.get();
        VaccinationRecordEntity existingRecord = petEntity.getVaccinationRecord();
        if (existingRecord == null)
            throw new EntityNotFoundException(ErrorMessage.VACCINATION_RECORD_NOT_FOUND);

        petEntity.setVaccinationRecord(null);
        petRepository.save(petEntity);
        vaccinationRecordRepository.deleteById(existingRecord.getId());

        log.info("Finished process to delete vaccination record of pet with id = ", petId);
    }

    private boolean validateVaccinationRecord(VaccinationRecordEntity vaccinationRecord) {
        return vaccinationRecord.getVaccinesApplied() != null && !vaccinationRecord.getVaccinesApplied().isEmpty();
    }
}