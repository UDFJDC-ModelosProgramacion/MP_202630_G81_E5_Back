package co.edu.udistrital.mdp.pets.services;


import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.TrialCohabitationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor 
@Slf4j
@Service 
public class TrialCohabitationService {
    final AdoptionRepository adoptionRepository;

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public TrialCohabitationEntity createTrialCohabitation(Long adoptionId, TrialCohabitationEntity trialCohabitationEntity) throws EntityNotFoundException, IllegalOperationException{
        log.info("Starting the process of creating a trial cohabitation for adoption with id = ", adoptionId);

        Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
        if (adoptionOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);

        AdoptionEntity adoptionEntity = adoptionOptional.get();

        if (adoptionEntity.getTrialCohabitation() != null)
            throw new IllegalOperationException(ErrorMessage.TRIAL_COHABITATION_ALREADY_EXISTS);

        if (!validateTrialCohabitation(trialCohabitationEntity))
            throw new IllegalOperationException(ErrorMessage.TRIAL_COHABITATION_NOT_VALID);

        if (trialCohabitationEntity.getEndDate() != null
                && !trialCohabitationEntity.getEndDate().after(trialCohabitationEntity.getStartDate()))
            throw new IllegalOperationException(ErrorMessage.TRIAL_COHABITATION_DATE_INVALID);

        adoptionEntity.setTrialCohabitation(trialCohabitationEntity);
        adoptionRepository.save(adoptionEntity);

        log.info("Finished process to create a trial cohabitation for adoption with id = ", adoptionId);
        return adoptionEntity.getTrialCohabitation();
    
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public TrialCohabitationEntity getTrialCohabitation(Long adoptionId) throws EntityNotFoundException{
        log.info("Starting process to fetch trial cohabitation of adoption with id = ", adoptionId);
        Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
        if (adoptionOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);

        AdoptionEntity adoptionEntity = adoptionOptional.get();
        if (adoptionEntity.getTrialCohabitation() == null)
            throw new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND);

        log.info("Finished process to fetch trial cohabitation of adoption with id = ", adoptionId);
        return adoptionEntity.getTrialCohabitation();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public TrialCohabitationEntity updateTrialCohabitation(Long adoptionId, TrialCohabitationEntity trialCohabitationEntity) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update trial cohabitation of adoption with id = ", adoptionId);
		Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
		if (adoptionOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);
 
		AdoptionEntity adoptionEntity = adoptionOptional.get();
		TrialCohabitationEntity existingTrialCohabitation = adoptionEntity.getTrialCohabitation();
		if (existingTrialCohabitation == null)
			throw new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND);

		if (!validateTrialCohabitation(trialCohabitationEntity))
			throw new IllegalOperationException(ErrorMessage.TRIAL_COHABITATION_NOT_VALID);

		if (trialCohabitationEntity.getEndDate() != null
				&& !trialCohabitationEntity.getEndDate().after(trialCohabitationEntity.getStartDate()))
			throw new IllegalOperationException(ErrorMessage.TRIAL_COHABITATION_DATE_INVALID);
 
		trialCohabitationEntity.setId(existingTrialCohabitation.getId());
		adoptionEntity.setTrialCohabitation(trialCohabitationEntity);
		adoptionRepository.save(adoptionEntity);
		log.info("Finished process to update trial cohabitation of adoption with id = ", adoptionId);
		return adoptionEntity.getTrialCohabitation();
	}

    @Transactional(rollbackFor = { EntityNotFoundException.class })
	public void deleteTrialCohabitation(Long adoptionId) throws EntityNotFoundException {
		log.info("Starting process to delete trial cohabitation of adoption with id = ", adoptionId);
		Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
		if (adoptionOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);
 
		AdoptionEntity adoptionEntity = adoptionOptional.get();
		if (adoptionEntity.getTrialCohabitation() == null)
			throw new EntityNotFoundException(ErrorMessage.TRIAL_COHABITATION_NOT_FOUND);
 
		adoptionEntity.setTrialCohabitation(null);
		adoptionRepository.save(adoptionEntity);
		log.info("Finished process to delete trial cohabitation of adoption with id = ", adoptionId);
	}

    private boolean validateTrialCohabitation(TrialCohabitationEntity trialCohabitation){
        return trialCohabitation.getStartDate() != null;
    }
    
}

