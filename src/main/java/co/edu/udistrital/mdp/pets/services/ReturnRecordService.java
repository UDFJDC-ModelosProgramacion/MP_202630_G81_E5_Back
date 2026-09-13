package co.edu.udistrital.mdp.pets.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReturnRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReturnRecordService {

    private static final String RETURNED_STATUS = "RETURNED";

    final AdoptionRepository adoptionRepository;
    final PetRepository petRepository;

    /**
     * Regla: la adoption debe existir y no tener ya un returnRecord; date y
     * reason son obligatorios; al crear se marca adoption.status = "RETURNED"
     * y pet.available = true.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ReturnRecordEntity createReturnRecord(Long adoptionId, ReturnRecordEntity returnRecordEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to create a return record for adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);

        if (adoptionEntity.getReturnRecord() != null)
            throw new IllegalOperationException(ErrorMessage.RETURN_RECORD_ALREADY_EXISTS);

        if (!validateReturnRecord(returnRecordEntity))
            throw new IllegalOperationException(ErrorMessage.RETURN_RECORD_NOT_VALID);

        adoptionEntity.setReturnRecord(returnRecordEntity);
        adoptionEntity.setStatus(RETURNED_STATUS);

        PetEntity pet = adoptionEntity.getPet();
        if (pet != null) {
            pet.setAvailable(true);
            petRepository.save(pet);
        }

        AdoptionEntity savedAdoption = adoptionRepository.save(adoptionEntity);

        log.info("Finished process to create a return record for adoption with id = {}", adoptionId);
        return savedAdoption.getReturnRecord();
    }

    /**
     * Regla: la adoption debe existir; excepción si no tiene returnRecord.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public ReturnRecordEntity getReturnRecord(Long adoptionId) throws EntityNotFoundException {
        log.info("Starting process to fetch return record of adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);

        if (adoptionEntity.getReturnRecord() == null)
            throw new EntityNotFoundException(ErrorMessage.RETURN_RECORD_NOT_FOUND);

        log.info("Finished process to fetch return record of adoption with id = {}", adoptionId);
        return adoptionEntity.getReturnRecord();
    }

    /**
     * Regla: la adoption y el returnRecord deben existir.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public ReturnRecordEntity updateReturnRecord(Long adoptionId, ReturnRecordEntity returnRecordEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update return record of adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);

        if (adoptionEntity.getReturnRecord() == null)
            throw new EntityNotFoundException(ErrorMessage.RETURN_RECORD_NOT_FOUND);

        if (!validateReturnRecord(returnRecordEntity))
            throw new IllegalOperationException(ErrorMessage.RETURN_RECORD_NOT_VALID);

        returnRecordEntity.setId(adoptionEntity.getReturnRecord().getId());
        adoptionEntity.setReturnRecord(returnRecordEntity);

        AdoptionEntity savedAdoption = adoptionRepository.save(adoptionEntity);

        log.info("Finished process to update return record of adoption with id = {}", adoptionId);
        return savedAdoption.getReturnRecord();
    }

    /**
     * Regla: la adoption y el returnRecord deben existir.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteReturnRecord(Long adoptionId) throws EntityNotFoundException {
        log.info("Starting process to delete return record of adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);

        if (adoptionEntity.getReturnRecord() == null)
            throw new EntityNotFoundException(ErrorMessage.RETURN_RECORD_NOT_FOUND);

        // orphanRemoval = true en Adoption.returnRecord: al desasociarlo se borra
        adoptionEntity.setReturnRecord(null);
        adoptionRepository.save(adoptionEntity);

        log.info("Finished process to delete return record of adoption with id = {}", adoptionId);
    }

    private AdoptionEntity getAdoptionOrThrow(Long adoptionId) throws EntityNotFoundException {
        Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
        if (adoptionOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);
        return adoptionOptional.get();
    }

    private boolean validateReturnRecord(ReturnRecordEntity returnRecord) {
        return returnRecord.getDate() != null && returnRecord.getReason() != null
                && !returnRecord.getReason().isEmpty();
    }
}
