package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class VeterinarianService {

    final VeterinarianRepository veterinarianRepository;
    final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = { IllegalOperationException.class })
    public VeterinarianEntity createVeterinarian(VeterinarianEntity veterinarianEntity)
            throws IllegalOperationException {
        log.info("Starting the process of creating a veterinarian");

        if (!validateVeterinarian(veterinarianEntity))
            throw new IllegalOperationException(ErrorMessage.VETERINARIAN_NOT_VALID);

        if (veterinarianEntity.getShelter() != null) {
            Optional<ShelterEntity> shelterOptional = shelterRepository
                    .findById(veterinarianEntity.getShelter().getId());
            if (shelterOptional.isEmpty())
                throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);
            veterinarianEntity.setShelter(shelterOptional.get());
        }

        log.info("Finished process to create a veterinarian");
        return veterinarianRepository.save(veterinarianEntity);
    }

    @Transactional
    public List<VeterinarianEntity> getVeterinarians() {
        log.info("Starting process to fetch all veterinarians");
        return veterinarianRepository.findAll();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public VeterinarianEntity getVeterinarian(Long veterinarianId) throws EntityNotFoundException {
        log.info("Starting process to fetch veterinarian with id = ", veterinarianId);

        Optional<VeterinarianEntity> veterinarianOptional = veterinarianRepository.findById(veterinarianId);
        if (veterinarianOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);

        log.info("Finished process to fetch veterinarian with id = ", veterinarianId);
        return veterinarianOptional.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public VeterinarianEntity updateVeterinarian(Long veterinarianId, VeterinarianEntity veterinarianEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update veterinarian with id = ", veterinarianId);

        Optional<VeterinarianEntity> veterinarianOptional = veterinarianRepository.findById(veterinarianId);
        if (veterinarianOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);

        if (!validateVeterinarian(veterinarianEntity))
            throw new IllegalOperationException(ErrorMessage.VETERINARIAN_NOT_VALID);

        VeterinarianEntity existingVeterinarian = veterinarianOptional.get();
        veterinarianEntity.setId(veterinarianId);
        veterinarianEntity.setShelter(existingVeterinarian.getShelter());

        log.info("Finished process to update veterinarian with id = ", veterinarianId);
        return veterinarianRepository.save(veterinarianEntity);
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public void deleteVeterinarian(Long veterinarianId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to delete veterinarian with id = ", veterinarianId);

        Optional<VeterinarianEntity> veterinarianOptional = veterinarianRepository.findById(veterinarianId);
        if (veterinarianOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);

        VeterinarianEntity veterinarianEntity = veterinarianOptional.get();

        if (!veterinarianEntity.getRegisteredEvents().isEmpty())
            throw new IllegalOperationException(ErrorMessage.VETERINARIAN_ASSOCIATED_LIFE_EVENTS);

        if (!veterinarianEntity.getAdoptions().isEmpty())
            throw new IllegalOperationException(ErrorMessage.VETERINARIAN_ASSOCIATED_ADOPTIONS);

        veterinarianRepository.deleteById(veterinarianId);
        log.info("Finished process to delete veterinarian with id = ", veterinarianId);
    }

    private boolean validateVeterinarian(VeterinarianEntity veterinarian) {
        return veterinarian.getName() != null && !veterinarian.getName().isEmpty()
                && veterinarian.getEmail() != null && !veterinarian.getEmail().isEmpty()
                && veterinarian.getSpecialty() != null && !veterinarian.getSpecialty().isEmpty();
    }
}