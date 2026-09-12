package co.edu.udistrital.mdp.pets.services;


import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor 
@Slf4j
@Service 
public class PetService {
    final PetRepository petRepository;
    final ShelterRepository shelterRepository;

    @Transactional(rollbackFor = {IllegalOperationException.class})
    public PetEntity createPet(PetEntity petEntity) throws IllegalOperationException{
        log.info("Starting the process of creating a pet");
        if(petEntity.getShelter() == null)
            throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);

        Optional<ShelterEntity> shelterOptional = shelterRepository.findById(petEntity.getShelter().getId());
        if (shelterOptional.isEmpty())
            throw new IllegalOperationException(ErrorMessage.SHELTER_NOT_VALID);

        if (!validatePet(petEntity))
            throw new IllegalOperationException(ErrorMessage.PET_NOT_VALID);

        if (petEntity.getAge() < 0)
            throw new IllegalOperationException(ErrorMessage.PET_AGE_INVALID);

        petEntity.setShelter(shelterOptional.get());
        log.info("Finished process to create a pet");
        return petRepository.save(petEntity);
    
    }

    @Transactional
    public List<PetEntity> getPets(){
        log.info("Starting process to fetch all pets");
        return petRepository.findAll();
    }

    @Transactional(rollbackFor = {EntityNotFoundException.class})
    public PetEntity getPet(Long petId) throws EntityNotFoundException{
        log.info("Starting process to fetch a pet with id = ", petId);
        Optional<PetEntity> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);

        log.info("Finished process to fetch a pet with id = ", petId);
        return petOptional.get();
    }

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public PetEntity updatePet(Long petId, PetEntity pet) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to update pet with id = ", petId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
 
		if (!validatePet(pet))
			throw new IllegalOperationException(ErrorMessage.PET_NOT_VALID);
 
		if (pet.getAge() < 0)
			throw new IllegalOperationException(ErrorMessage.PET_AGE_INVALID);
 
		PetEntity existingPet = petOptional.get();
		pet.setId(petId);
		pet.setShelter(existingPet.getShelter());
		log.info("Finished process to update pet with id = ", petId);
		return petRepository.save(pet);
	}

    @Transactional(rollbackFor = { EntityNotFoundException.class, IllegalOperationException.class })
	public void deletePet(Long petId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Starting process to delete pet with id = ", petId);
		Optional<PetEntity> petOptional = petRepository.findById(petId);
		if (petOptional.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PET_NOT_FOUND);
 
		PetEntity petEntity = petOptional.get();
 
		if (!petEntity.getAdoptions().isEmpty())
			throw new IllegalOperationException(ErrorMessage.PET_ASSOCIATED_ADOPTIONS);
 
		if (!petEntity.getPhotos().isEmpty())
			throw new IllegalOperationException(ErrorMessage.PET_ASSOCIATED_PHOTOS);
 
		petRepository.deleteById(petId);
		log.info("Finished process to delete pet with id = ", petId);
	}

    private boolean validatePet(PetEntity pet){
        return pet.getName() != null && !pet.getName().isEmpty() && pet.getSpecies() != null && !pet.getSpecies().isEmpty();
    }
    
}
