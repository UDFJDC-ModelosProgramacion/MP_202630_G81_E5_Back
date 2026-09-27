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

	final AdoptionRepository adoptionRepository;

	final PetRepository petRepository;

	final AdopterRepository adopterRepository;

	final VeterinarianRepository veterinarianRepository;

	/**
	 * Crea una adopción. El pet debe existir y estar disponible; al crearla se
	 * marca el pet como no disponible.
	 */
	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public AdoptionEntity createAdoption(AdoptionEntity adoptionEntity)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to create adoption");

		if (adoptionEntity.getPet() == null)
			throw new IllegalOperationException(ErrorMessage.PET_NOT_VALID);
		Optional<PetEntity> petOptional = petRepository.findById(adoptionEntity.getPet().getId());
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

		if (adoptionEntity.getAdopter() == null)
			throw new IllegalOperationException(ErrorMessage.ADOPTER_NOT_VALID);
		Optional<AdopterEntity> adopterOptional = adopterRepository.findById(adoptionEntity.getAdopter().getId());
		if (adopterOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTER_NOT_FOUND);

		if (adoptionEntity.getResponsibleVeterinarian() == null)
			throw new IllegalOperationException(ErrorMessage.VETERINARIAN_NOT_VALID);
		Optional<VeterinarianEntity> vetOptional = veterinarianRepository
				.findById(adoptionEntity.getResponsibleVeterinarian().getId());
		if (vetOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.VETERINARIAN_NOT_FOUND);

		if (adoptionEntity.getDate() == null)
			throw new IllegalOperationException(ErrorMessage.ADOPTION_NOT_VALID);

		PetEntity petEntity = petOptional.get();
		if (!petEntity.isAvailable())
			throw new IllegalOperationException(ErrorMessage.ADOPTION_PET_NOT_AVAILABLE);

		if (adoptionEntity.getStatus() == null || adoptionEntity.getStatus().isEmpty())
			adoptionEntity.setStatus("IN_PROGRESS");

		petEntity.setAvailable(false);
		petRepository.save(petEntity);

		adoptionEntity.setPet(petEntity);
		adoptionEntity.setAdopter(adopterOptional.get());
		adoptionEntity.setResponsibleVeterinarian(vetOptional.get());

		log.info("Finished process to create adoption");
		return adoptionRepository.save(adoptionEntity);
	}

	@Transactional
	public List<AdoptionEntity> getAdoptions() {
		log.info("Starting process to fetch all adoptions");
		return adoptionRepository.findAll();
	}

	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public AdoptionEntity getAdoption(Long adoptionId) throws EntityNotFoundException {
		log.info("Starting process to fetch adoption with id = {0}", adoptionId);
		Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
		if (adoptionOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);

		log.info("Finished process to fetch adoption with id = {0}", adoptionId);
		return adoptionOptional.get();
	}

	/**
	 * Actualiza una adopción. No permite reasignar pet, adopter ni
	 * responsibleVeterinarian desde este método.
	 */
	@Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public AdoptionEntity updateAdoption(Long adoptionId, AdoptionEntity adoption)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update adoption with id = {0}", adoptionId);
		Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
		if (adoptionOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);

		if (adoption.getStatus() == null || adoption.getStatus().isEmpty())
			throw new IllegalOperationException(ErrorMessage.ADOPTION_STATUS_INVALID);

		AdoptionEntity existingAdoption = adoptionOptional.get();
		adoption.setId(adoptionId);
		adoption.setPet(existingAdoption.getPet());
		adoption.setAdopter(existingAdoption.getAdopter());
		adoption.setResponsibleVeterinarian(existingAdoption.getResponsibleVeterinarian());
		adoption.setReturnRecord(existingAdoption.getReturnRecord());
		adoption.setTrialCohabitation(existingAdoption.getTrialCohabitation());

		log.info("Finished process to update adoption with id = {0}", adoptionId);
		return adoptionRepository.save(adoption);
	}

	/**
	 * Elimina una adopción. returnRecord y trialCohabitation se eliminan en
	 * cascada (cascade=ALL, orphanRemoval). Si el pet no queda con otra adopción
	 * activa, se libera (available = true).
	 */
	@Transactional(rollbackFor = { EntityNotFoundException.class })
	public void deleteAdoption(Long adoptionId) throws EntityNotFoundException {
		log.info("Starting process to delete adoption with id = {0}", adoptionId);
		Optional<AdoptionEntity> adoptionOptional = adoptionRepository.findById(adoptionId);
		if (adoptionOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ADOPTION_NOT_FOUND);

		AdoptionEntity adoptionEntity = adoptionOptional.get();
		PetEntity petEntity = adoptionEntity.getPet();

		adoptionRepository.deleteById(adoptionId);

		if (petEntity != null) {
			petEntity.setAvailable(true);
			petRepository.save(petEntity);
		}
		log.info("Finished process to delete adoption with id = {0}", adoptionId);
	}
}
