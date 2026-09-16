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
        log.info("Starting the process of creating a life event for pet with id = ", petId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        if (!validateLifeEvent(lifeEventEntity))
            throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_VALID);

        if (lifeEventEntity.getVeterinarian() != null) {
            Optional<VeterinarianEntity> veterinarianOptional = veterinarianRepository
                    .findById(lifeEventEntity.getVeterinarian().getId());
            if (veterinarianOptional.isEmpty())
                throw new IllegalOperationException(ErrorMessage.VETERINARIAN_NOT_VALID);
            lifeEventEntity.setVeterinarian(veterinarianOptional.get());
        }

        lifeEventEntity.setPet(petOptional.get());
        log.info("Finished process to create a life event for pet with id = ", petId);
        return lifeEventRepository.save(lifeEventEntity);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public List<LifeEventEntity> getLifeEvents(Long petId) throws EntityNotFoundException {
        log.info("Starting process to fetch all life events of pet with id = ", petId);
        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        log.info("Finished process to fetch all life events of pet with id = ", petId);
        return petOptional.get().getLifeEvents();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public LifeEventEntity getLifeEvent(Long petId, Long lifeEventId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to fetch life event with id = ", lifeEventId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        Optional<LifeEventEntity> lifeEventOptional = lifeEventRepository.findById(lifeEventId);
        if (lifeEventOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.LIFE_EVENT_NOT_FOUND);

        if (!lifeEventOptional.get().getPet().getId().equals(petId))
            throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_ASSOCIATED_TO_PET);

        log.info("Finished process to fetch life event with id = ", lifeEventId);
        return lifeEventOptional.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public LifeEventEntity updateLifeEvent(Long petId, Long lifeEventId, LifeEventEntity lifeEventEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update life event with id = ", lifeEventId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        Optional<LifeEventEntity> lifeEventOptional = lifeEventRepository.findById(lifeEventId);
        if (lifeEventOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.LIFE_EVENT_NOT_FOUND);

        if (!lifeEventOptional.get().getPet().getId().equals(petId))
            throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_ASSOCIATED_TO_PET);

        if (!validateLifeEvent(lifeEventEntity))
            throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_VALID);

        lifeEventEntity.setId(lifeEventId);
        lifeEventEntity.setPet(petOptional.get());
        log.info("Finished process to update life event with id = ", lifeEventId);
        return lifeEventRepository.save(lifeEventEntity);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public void deleteLifeEvent(Long petId, Long lifeEventId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to delete life event with id = ", lifeEventId);

        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        Optional<LifeEventEntity> lifeEventOptional = lifeEventRepository.findById(lifeEventId);
        if (lifeEventOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.LIFE_EVENT_NOT_FOUND);

        if (!lifeEventOptional.get().getPet().getId().equals(petId))
            throw new IllegalOperationException(ErrorMessage.LIFE_EVENT_NOT_ASSOCIATED_TO_PET);

        lifeEventRepository.deleteById(lifeEventId);
        log.info("Finished process to delete life event with id = ", lifeEventId);
    }

    private boolean validateLifeEvent(LifeEventEntity lifeEvent) {
        return lifeEvent.getType() != null && !lifeEvent.getType().isEmpty() && lifeEvent.getDate() != null;
    }
}