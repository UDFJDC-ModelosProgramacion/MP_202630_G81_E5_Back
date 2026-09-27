package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class ShelterService {
    final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = {IllegalOperationException.class})
    public ShelterEntity createShelter(ShelterEntity shelterEntity) throws IllegalOperationException {
        log.info("Starting the process of creating a shelter");
        if (!validateShelter(shelterEntity))
            throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);

        log.info("Finished process to create a shelter");
        return shelterRepository.save(shelterEntity);
    }

    @Transactional
    public List<ShelterEntity> getShelters() {
        log.info("Starting process to fetch all shelters");
        return shelterRepository.findAll();
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public ShelterEntity getShelter(Long shelterId) throws EntityNotFoundException {
        log.info("Starting process to fetch a shelter with id = ", shelterId);
        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
        if (shelterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

        log.info("Finished process to fetch a shelter with id = ", shelterId);
        return shelterOptional.get();
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, IllegalOperationException.class})
    public ShelterEntity updateShelter(Long shelterId, ShelterEntity shelter) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update shelter with id = ", shelterId);
        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
        if (shelterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

        if (!validateShelter(shelter))
            throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);

        shelter.setId(shelterId);
        log.info("Finished process to update shelter with id = ", shelterId);
        return shelterRepository.save(shelter);
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class, IllegalOperationException.class})
    public void deleteShelter(Long shelterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to delete shelter with id = ", shelterId);
        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(shelterId);
        if (shelterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.SHELTER_NOT_FOUND);

        ShelterEntity shelterEntity = shelterOptional.get();

        if (!shelterEntity.getPets().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_PETS);

        if (!shelterEntity.getVeterinarians().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_VETERINARIANS);

        if (!shelterEntity.getNotifications().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_NOTIFICATIONS);

        if (!shelterEntity.getPhotos().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_PHOTOS);

        if (!shelterEntity.getVideos().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_VIDEOS);

        if (!shelterEntity.getShelterEvents().isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_ASSOCIATED_SHELTER_EVENTS);

        shelterRepository.deleteById(shelterId);
        log.info("Finished process to delete shelter with id = ", shelterId);
    }

    private boolean validateShelter(ShelterEntity shelter) {
        return shelter.getName() != null && !shelter.getName().isEmpty()
                && shelter.getCity() != null && !shelter.getCity().isEmpty();
    }

}