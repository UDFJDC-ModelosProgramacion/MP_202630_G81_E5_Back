package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdoptionService {

    /*
     * El enunciado no especifica el conjunto exacto de valores válidos para
     * "status". Se asume este conjunto; si el equipo ya definió otro (por
     * ejemplo un enum), ajústalo aquí, en un único lugar.
     */
    private static final List<String> VALID_STATUSES = List.of("PENDING", "APPROVED", "ACTIVE", "RETURNED",
            "CANCELLED");
    // Estados "terminales": ya no cuentan como adopción activa sobre la mascota
    private static final List<String> TERMINAL_STATUSES = List.of("RETURNED", "CANCELLED");

    final AdoptionRepository adoptionRepository;
    final PetRepository petRepository;
    final AdopterRepository adopterRepository;
    final VeterinarianRepository veterinarianRepository;

    /**
     * Regla: pet, adopter y responsibleVeterinarian deben existir; el pet debe
     * estar disponible (available = true); al crear se marca
     * pet.available = false; date obligatoria.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public AdoptionEntity createAdoption(AdoptionEntity adoptionEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting the process of creating an adoption");

        if (adoptionEntity.getDate() == null)
            throw new IllegalOperationException(ErrorMessage.ADOPTION_NOT_VALID);

        if (adoptionEntity.getPet() == null || adoptionEntity.getAdopter() == null
                || adoptionEntity.getResponsibleVeterinarian() == null)
            throw new IllegalOperationException(ErrorMessage.ADOPTION_NOT_VALID);

        Optional<PetEntity> petOptional = petRepository.findById(adoptionEntity.getPet().getId());
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adoptionEntity.getAdopter().getId());
        if (adopterOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

        Optional<VeterinarianEntity> vetOptional = veterinarianRepository
                .findById(adoptionEntity.getResponsibleVeterinarian().getId());
        if (vetOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);

        PetEntity pet = petOptional.get();
        if (!pet.isAvailable())
            throw new IllegalOperationException(ErrorMessage.ADOPTION_PET_NOT_AVAILABLE);

        adoptionEntity.setPet(pet);
        adoptionEntity.setAdopter(adopterOptional.get());
        adoptionEntity.setResponsibleVeterinarian(vetOptional.get());

        pet.setAvailable(false);
        petRepository.save(pet);

        log.info("Finished process to create an adoption");
        return adoptionRepository.save(adoptionEntity);
    }

    @Transactional
    public List<AdoptionEntity> getAdoptions() {
        log.info("Starting process to fetch all adoptions");
        return adoptionRepository.findAll();
    }

    /**
     * Regla: excepción si no existe.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public AdoptionEntity getAdoption(Long adoptionId) throws EntityNotFoundException {
        log.info("Starting process to fetch adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);

        log.info("Finished process to fetch adoption with id = {}", adoptionId);
        return adoptionEntity;
    }

    /**
     * Regla: la adoption debe existir; no se permite reasignar pet ni adopter
     * desde este método; status debe ser un valor válido.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
    public AdoptionEntity updateAdoption(Long adoptionId, AdoptionEntity adoptionEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Starting process to update adoption with id = {}", adoptionId);

        AdoptionEntity existingAdoption = getAdoptionOrThrow(adoptionId);

        if (!isValidStatus(adoptionEntity.getStatus()))
            throw new IllegalOperationException(ErrorMessage.ADOPTION_STATUS_INVALID);

        adoptionEntity.setId(adoptionId);
        // pet y adopter no se pueden reasignar desde este método
        adoptionEntity.setPet(existingAdoption.getPet());
        adoptionEntity.setAdopter(existingAdoption.getAdopter());
        // returnRecord y trialCohabitation son OneToOne con orphanRemoval = true;
        // si no se preservan aquí, guardarían null y se borrarían en cascada
        adoptionEntity.setReturnRecord(existingAdoption.getReturnRecord());
        adoptionEntity.setTrialCohabitation(existingAdoption.getTrialCohabitation());

        log.info("Finished process to update adoption with id = {}", adoptionId);
        return adoptionRepository.save(adoptionEntity);
    }

    /**
     * Regla: returnRecord y trialCohabitation tienen cascade = ALL,
     * orphanRemoval y se eliminan en cascada; al eliminar, si el pet no tiene
     * otra adopción activa se restaura pet.available = true.
     */
    @Transactional(rollbackFor = { EntityNotFoundException.class })
    public void deleteAdoption(Long adoptionId) throws EntityNotFoundException {
        log.info("Starting process to delete adoption with id = {}", adoptionId);

        AdoptionEntity adoptionEntity = getAdoptionOrThrow(adoptionId);
        Long petId = adoptionEntity.getPet() != null ? adoptionEntity.getPet().getId() : null;

        // returnRecord y trialCohabitation se eliminan automáticamente por cascada
        adoptionRepository.deleteById(adoptionId);

        if (petId != null)
            restorePetAvailabilityIfNoActiveAdoptions(petId);

        log.info("Finished process to delete adoption with id = {}", adoptionId);
    }

    private void restorePetAvailabilityIfNoActiveAdoptions(Long petId) {
        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            return;

        PetEntity pet = petOptional.get();
        boolean hasActiveAdoption = pet.getAdoptions().stream().anyMatch(this::isActiveAdoption);
        if (!hasActiveAdoption) {
            pet.setAvailable(true);
            petRepository.save(pet);
        }
    }

    private boolean isActiveAdoption(AdoptionEntity adoption) {
        String status = adoption.getStatus();
        return status == null || !TERMINAL_STATUSES.contains(status.toUpperCase());
    }

    private boolean isValidStatus(String status) {
        return status != null && VALID_STATUSES.contains(status.toUpperCase());
    }

    private AdoptionEntity getAdoptionOrThrow(Long adoptionId) throws EntityNotFoundException {
        Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
        if (adoptionOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);
        return adoptionOptional.get();
    }
}