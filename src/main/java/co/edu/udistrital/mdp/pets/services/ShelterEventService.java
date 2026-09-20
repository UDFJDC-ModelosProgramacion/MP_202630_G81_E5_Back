package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterEventRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ShelterEventService {
    final ShelterEventRepository shelterEventRepository;

	final ShelterRepository shelterRepository;

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ShelterEventEntity createShelterEvent(Long shelterId, ShelterEventEntity shelterEventEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create shelter event for shelter with id = {0}", shelterId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		if (!validateShelterEvent(shelterEventEntity))
			throw new IllegalOperationException(ErrorMessage.SHELTER_EVENT_NOT_VALID);

		shelterEventEntity.setShelter(shelterOptional.get());
		log.info("Finished process to create shelter event for shelter with id = {0}", shelterId);
		return shelterEventRepository.save(shelterEventEntity);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<ShelterEventEntity> getShelterEvents(Long shelterId) throws EntityNotFoundException {
		log.info("Starting process to fetch shelter events of shelter with id = {0}", shelterId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		log.info("Finished process to fetch shelter events of shelter with id = {0}", shelterId);
		return shelterOptional.get().getShelterEvents();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ShelterEventEntity getShelterEvent(Long shelterId, Long shelterEventId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
		Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
		if (shelterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

		Optional<ShelterEventEntity> shelterEventOptional = shelterEventRepository.findById(shelterEventId);
		if (shelterEventOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.SHELTER_EVENT_NOT_FOUND);

		ShelterEventEntity shelterEventEntity = shelterEventOptional.get();
		if (shelterEventEntity.getShelter() == null || !shelterEventEntity.getShelter().getId().equals(shelterId))
			throw new IllegalOperationException(ErrorMessage.SHELTER_EVENT_NOT_ASSOCIATED_TO_SHELTER);

		log.info("Finished process to fetch shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
		return shelterEventEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public ShelterEventEntity updateShelterEvent(Long shelterId, Long shelterEventId, ShelterEventEntity shelterEvent)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
		ShelterEventEntity existingShelterEvent = getShelterEvent(shelterId, shelterEventId);

		if (!validateShelterEvent(shelterEvent))
			throw new IllegalOperationException(ErrorMessage.SHELTER_EVENT_NOT_VALID);

		shelterEvent.setId(existingShelterEvent.getId());
		shelterEvent.setShelter(existingShelterEvent.getShelter());
		log.info("Finished process to update shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
		return shelterEventRepository.save(shelterEvent);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteShelterEvent(Long shelterId, Long shelterEventId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
		ShelterEventEntity shelterEventEntity = getShelterEvent(shelterId, shelterEventId);
		shelterEventRepository.deleteById(shelterEventEntity.getId());
		log.info("Finished process to delete shelter event with id = {0} of shelter with id = " + shelterId,
				shelterEventId);
	}

	private boolean validateShelterEvent(ShelterEventEntity shelterEvent) {
		return shelterEvent.getTitle() != null && !shelterEvent.getTitle().isEmpty()
				&& shelterEvent.getDate() != null;
	}

}