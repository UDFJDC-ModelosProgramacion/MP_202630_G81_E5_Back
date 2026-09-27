package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.LifeEventEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.LifeEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class LifeEventService {

	final LifeEventRepository lifeEventRepository;

	final PetRepository petRepository;

	final VeterinarianRepository veterinarianRepository;

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public LifeEventEntity createLifeEvent(Long petId, LifeEventEntity lifeEventEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create life event for pet with id = {0}", petId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		if (!validateLifeEvent(lifeEventEntity))
			throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_VALID);

		if (lifeEventEntity.getVeterinarian() != null) {
			Optional<VeterinarianEntity> vetOptional = veterinarianRepository
					.findById(lifeEventEntity.getVeterinarian().getId());
			if (vetOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);
			lifeEventEntity.setVeterinarian(vetOptional.get());
		}

		lifeEventEntity.setPet(petOptional.get());
		log.info("Finished process to create life event for pet with id = {0}", petId);
		return lifeEventRepository.save(lifeEventEntity);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public List<LifeEventEntity> getLifeEvents(Long petId) throws EntityNotFoundException {
		log.info("Starting process to fetch life events of pet with id = {0}", petId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		log.info("Finished process to fetch life events of pet with id = {0}", petId);
		return petOptional.get().getLifeEvents();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public LifeEventEntity getLifeEvent(Long petId, Long lifeEventId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to fetch life event with id = {0} of pet with id = " + petId, lifeEventId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		Optional<LifeEventEntity> lifeEventOptional = lifeEventRepository.findById(lifeEventId);
		if (lifeEventOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.LIFE_EVENT_NOT_FOUND);

		LifeEventEntity lifeEventEntity = lifeEventOptional.get();
		if (lifeEventEntity.getPet() == null || !lifeEventEntity.getPet().getId().equals(petId))
			throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_ASSOCIATED_TO_PET);

		log.info("Finished process to fetch life event with id = {0} of pet with id = " + petId, lifeEventId);
		return lifeEventEntity;
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public LifeEventEntity updateLifeEvent(Long petId, Long lifeEventId, LifeEventEntity lifeEvent)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update life event with id = {0} of pet with id = " + petId, lifeEventId);
		LifeEventEntity existingLifeEvent = getLifeEvent(petId, lifeEventId);

		if (!validateLifeEvent(lifeEvent))
			throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_VALID);

		if (lifeEvent.getVeterinarian() != null) {
			Optional<VeterinarianEntity> vetOptional = veterinarianRepository
					.findById(lifeEvent.getVeterinarian().getId());
			if (vetOptional.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);
			lifeEvent.setVeterinarian(vetOptional.get());
		}

		lifeEvent.setId(existingLifeEvent.getId());
		lifeEvent.setPet(existingLifeEvent.getPet());
		log.info("Finished process to update life event with id = {0} of pet with id = " + petId, lifeEventId);
		return lifeEventRepository.save(lifeEvent);
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deleteLifeEvent(Long petId, Long lifeEventId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete life event with id = {0} of pet with id = " + petId, lifeEventId);
		LifeEventEntity lifeEventEntity = getLifeEvent(petId, lifeEventId);

		PetEntity pet = lifeEventEntity.getPet();
		pet.getLifeEvents().remove(lifeEventEntity);
		petRepository.save(pet);

		log.info("Finished process to delete life event with id = {0} of pet with id = " + petId, lifeEventId);
	}

	private boolean validateLifeEvent(LifeEventEntity lifeEvent) {
		return lifeEvent.getType() != null && !lifeEvent.getType().isEmpty() && lifeEvent.getDate() != null;
	}
}